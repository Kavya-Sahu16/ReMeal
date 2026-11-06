package com.kavya.remeal.donation;

import com.kavya.remeal.donation.dto.DonationRequest;
import com.kavya.remeal.donation.dto.DonationResponse;
import com.kavya.remeal.user.Role;
import com.kavya.remeal.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;

    // PHASE 4: CREATE DONATION
    @Transactional
    public DonationResponse createDonation(
            User user, DonationRequest request) {

        checkDonor(user);
        validateDates(request);

        Donation donation = new Donation();
        donation.setDonor(user);

        updateDonationFields(donation, request);
        donation.setStatus(DonationStatus.AVAILABLE);

        return mapToResponse(donationRepository.save(donation));
    }

    // PHASE 4: GET MY DONATIONS
    @Transactional(readOnly = true)
    public List<DonationResponse> getMyDonations(User user) {

        checkDonor(user);

        return donationRepository
                .findAllByDonorIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // PHASE 4: GET A SPECIFIC DONATION
    @Transactional(readOnly = true)
    public DonationResponse getDonation(Long id, User user) {

        checkDonor(user);

        Donation donation = findMyDonation(id, user.getId());

        return mapToResponse(donation);
    }

    // PHASE 4: UPDATE DONATION
    @Transactional
    public DonationResponse updateDonation(
            Long id, User user, DonationRequest request) {

        checkDonor(user);
        validateDates(request);

        Donation donation = findMyDonation(id, user.getId());

        if (donation.getStatus() != DonationStatus.AVAILABLE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only available donations can be updated"
            );
        }

        updateDonationFields(donation, request);

        return mapToResponse(donationRepository.save(donation));
    }

    // PHASE 4: DELETE DONATION
    @Transactional
    public void deleteDonation(Long id, User user) {

        checkDonor(user);

        Donation donation = findMyDonation(id, user.getId());

        if (donation.getStatus() != DonationStatus.AVAILABLE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only available donations can be deleted"
            );
        }

        donationRepository.delete(donation);
    }

    // PHASE 5: BROWSE AND FILTER DONATIONS
    @Transactional(readOnly = true)
    public List<DonationResponse> browseDonations(
            User user,
            String city,
            DonationCategory category,
            BigDecimal minQuantity,
            BigDecimal maxQuantity,
            LocalDateTime expiryFrom,
            LocalDateTime expiryTo,
            String foodName) {

        checkRecipient(user);

        // Validate quantity range
        if (minQuantity != null && minQuantity.signum() < 0
                || maxQuantity != null && maxQuantity.signum() < 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantity filters cannot be negative"
            );
        }

        if (minQuantity != null && maxQuantity != null
                && minQuantity.compareTo(maxQuantity) > 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Minimum quantity cannot exceed maximum quantity"
            );
        }

        // Validate expiry range
        if (expiryFrom != null && expiryTo != null
                && expiryFrom.isAfter(expiryTo)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Expiry start cannot be after expiry end"
            );
        }

        // Start with mandatory conditions
        Specification<Donation> specification =
                DonationSpecification
                        .hasStatus(DonationStatus.AVAILABLE)
                        .and(DonationSpecification
                                .isNotExpired(LocalDateTime.now()));

        // Add optional filters
        if (city != null && !city.isBlank()) {
            specification = specification.and(
                    DonationSpecification.hasCity(city)
            );
        }

        if (category != null) {
            specification = specification.and(
                    DonationSpecification.hasCategory(category)
            );
        }

        if (minQuantity != null) {
            specification = specification.and(
                    DonationSpecification
                            .hasMinimumQuantity(minQuantity)
            );
        }

        if (maxQuantity != null) {
            specification = specification.and(
                    DonationSpecification
                            .hasMaximumQuantity(maxQuantity)
            );
        }

        if (expiryFrom != null) {
            specification = specification.and(
                    DonationSpecification.expiresAfter(expiryFrom)
            );
        }

        if (expiryTo != null) {
            specification = specification.and(
                    DonationSpecification.expiresBefore(expiryTo)
            );
        }

        if (foodName != null && !foodName.isBlank()) {
            specification = specification.and(
                    DonationSpecification.hasFoodName(foodName)
            );
        }

        return donationRepository.findAll(
                        specification,
                        Sort.by(Sort.Direction.DESC, "createdAt")
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // PHASE 5: VIEW AVAILABLE DONATION DETAILS
    @Transactional(readOnly = true)
    public DonationResponse getAvailableDonation(Long id, User user) {

        checkRecipient(user);

        Donation donation = donationRepository
                .findByIdAndStatusAndExpiryDateTimeAfter(
                        id,
                        DonationStatus.AVAILABLE,
                        LocalDateTime.now()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Available donation not found"
                ));

        return mapToResponse(donation);
    }

    // COMMON METHODS

    private void checkDonor(User user) {

        if (user.getRole() != Role.DONOR) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only donors can manage donations"
            );
        }
    }

    private void checkRecipient(User user) {

        if (user.getRole() != Role.RECIPIENT) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recipients can browse donations"
            );
        }
    }

    private Donation findMyDonation(Long id, Long donorId) {

        return donationRepository.findByIdAndDonorId(id, donorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Donation not found"
                ));
    }

    private void validateDates(DonationRequest request) {

        if (!request.expiryDateTime()
                .isAfter(request.preparationDateTime())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Expiry date must be after preparation date"
            );
        }
    }

    private void updateDonationFields(
            Donation donation, DonationRequest request) {

        donation.setFoodName(request.foodName());
        donation.setCategory(request.category());
        donation.setDescription(request.description());
        donation.setQuantity(request.quantity());
        donation.setQuantityUnit(request.quantityUnit());
        donation.setPickupAddress(request.pickupAddress());
        donation.setCity(request.city());
        donation.setPreparationDateTime(request.preparationDateTime());
        donation.setExpiryDateTime(request.expiryDateTime());
    }

    private DonationResponse mapToResponse(Donation donation) {

        return new DonationResponse(
                donation.getId(),
                donation.getDonor().getId(),
                donation.getFoodName(),
                donation.getCategory(),
                donation.getDescription(),
                donation.getQuantity(),
                donation.getQuantityUnit(),
                donation.getPickupAddress(),
                donation.getCity(),
                donation.getPreparationDateTime(),
                donation.getExpiryDateTime(),
                donation.getStatus(),
                donation.getCreatedAt()
        );
    }
}