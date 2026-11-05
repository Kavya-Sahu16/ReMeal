package com.kavya.remeal.donation;

import com.kavya.remeal.donation.dto.DonationRequest;
import com.kavya.remeal.donation.dto.DonationResponse;
import com.kavya.remeal.user.Role;
import com.kavya.remeal.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;

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

    @Transactional(readOnly = true)
    public List<DonationResponse> getMyDonations(User user) {

        checkDonor(user);

        return donationRepository
                .findAllByDonorIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DonationResponse getDonation(Long id, User user) {

        checkDonor(user);

        Donation donation = findMyDonation(id, user.getId());

        return mapToResponse(donation);
    }

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

    private void checkDonor(User user) {

        if (user.getRole() != Role.DONOR) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only donors can manage donations"
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