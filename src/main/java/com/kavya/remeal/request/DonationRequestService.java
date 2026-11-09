package com.kavya.remeal.request;

import com.kavya.remeal.donation.*;
import com.kavya.remeal.request.dto.CreateRequest;
import com.kavya.remeal.request.dto.RequestResponse;
import com.kavya.remeal.user.Role;
import com.kavya.remeal.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import com.kavya.remeal.user.UserRepository;

@Service
@RequiredArgsConstructor
public class DonationRequestService {

    private final DonationRequestRepository requestRepository;
    private final DonationRepository donationRepository;
    private final UserRepository userRepository;

    // RECIPIENT: REQUEST A DONATION
    @Transactional
    public RequestResponse createRequest(
            User user, CreateRequest request) {

        checkRecipient(user);

        Donation donation = donationRepository.findById(request.donationId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Donation not found"));

        if (donation.getDonor().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot request your own donation");
        }

        if (donation.getStatus() != DonationStatus.AVAILABLE
                || !donation.getExpiryDateTime().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Donation is no longer available");
        }

        boolean alreadyRequested =
                requestRepository
                        .existsByDonationIdAndRecipientIdAndRequestStatusIn(
                                donation.getId(),
                                user.getId(),
                                List.of(RequestStatus.PENDING,
                                        RequestStatus.APPROVED));

        if (alreadyRequested) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You have already requested this donation");
        }

        DonationRequest donationRequest = new DonationRequest();
        donationRequest.setDonation(donation);
        donationRequest.setRecipient(user);
        donationRequest.setRequestStatus(RequestStatus.PENDING);

        return mapToResponse(requestRepository.save(donationRequest));
    }

    // RECIPIENT: VIEW SENT REQUESTS
    @Transactional(readOnly = true)
    public List<RequestResponse> getMyRequests(User user) {

        checkRecipient(user);

        return requestRepository
                .findAllByRecipientIdOrderByRequestedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // DONOR: VIEW RECEIVED REQUESTS
    @Transactional(readOnly = true)
    public List<RequestResponse> getReceivedRequests(User user) {

        checkDonor(user);

        return requestRepository
                .findAllByDonation_Donor_IdOrderByRequestedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // DONOR: APPROVE REQUEST
    @Transactional
    public RequestResponse approveRequest(Long requestId, User user) {

        checkDonor(user);

        DonationRequest request = findRequest(requestId);

        Donation donation = donationRepository
                .findByIdForUpdate(request.getDonation().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Donation not found"));

        checkDonationOwner(donation, user);
        checkPending(request);

        if (donation.getStatus() != DonationStatus.AVAILABLE
                || !donation.getExpiryDateTime().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Donation is no longer available");
        }

        request.setRequestStatus(RequestStatus.APPROVED);
        request.setApprovedAt(LocalDateTime.now());

        donation.setStatus(DonationStatus.CLAIMED);

        // Reject all other pending requests for this donation.
        List<DonationRequest> otherRequests =
                requestRepository.findAllByDonationIdAndRequestStatus(
                        donation.getId(), RequestStatus.PENDING);

        for (DonationRequest other : otherRequests) {
            if (!other.getId().equals(request.getId())) {
                other.setRequestStatus(RequestStatus.REJECTED);
            }
        }

        requestRepository.saveAll(otherRequests);

        return mapToResponse(requestRepository.save(request));
    }

    // DONOR: REJECT REQUEST
    @Transactional
    public RequestResponse rejectRequest(Long requestId, User user) {

        checkDonor(user);

        DonationRequest request = findRequest(requestId);

        Donation donation = donationRepository
                .findByIdForUpdate(request.getDonation().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Donation not found"));

        checkDonationOwner(donation, user);
        checkPending(request);

        request.setRequestStatus(RequestStatus.REJECTED);

        return mapToResponse(requestRepository.save(request));
    }

    // DONOR: ASSIGN VOLUNTEER
    @Transactional
    public RequestResponse assignVolunteer(Long requestId, Long volunteerId, User donor) {

        checkDonor(donor);

        DonationRequest request = findRequest(requestId);

        if (!request.getDonation().getDonor().getId().equals(donor.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                "You cannot assign volunteers to another donor's request");
        }

        if (request.getRequestStatus() != RequestStatus.APPROVED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Volunteer can only be assigned to approved requests");
        }

        if (request.getAssignmentStatus() != AssignmentStatus.NOT_ASSIGNED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A volunteer has already been assigned");
        }

        User volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Volunteer not found"));

        if (volunteer.getRole() != Role.VOLUNTEER) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selected user is not a volunteer");
        }

        request.setVolunteer(volunteer);
        request.setAssignmentStatus(AssignmentStatus.ASSIGNED);

        return mapToResponse(requestRepository.save(request));
}


    // VOLUNTEER: VIEW ASSIGNMENTS
    @Transactional(readOnly = true)
    public List<RequestResponse> getMyAssignments(User volunteer) {

        checkVolunteer(volunteer);

        return requestRepository
                .findAllByVolunteerIdOrderByRequestedAtDesc(volunteer.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


// VOLUNTEER: ACCEPT ASSIGNMENT
    @Transactional
    public RequestResponse acceptAssignment(Long requestId, User volunteer) {

        checkVolunteer(volunteer);

    DonationRequest request = findRequest(requestId);

        if (request.getVolunteer() == null
                || !request.getVolunteer().getId().equals(volunteer.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "This assignment does not belong to you");
        }

        if (request.getAssignmentStatus() != AssignmentStatus.ASSIGNED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only assigned deliveries can be accepted");
        }

        request.setAssignmentStatus(AssignmentStatus.ACCEPTED);

        return mapToResponse(requestRepository.save(request));
    }


    // COMMON ROLE CHECK
    private void checkVolunteer(User user) {

        if (user.getRole() != Role.VOLUNTEER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only volunteers can access this resource");
        }
    
    }
    // COMMON METHODS

    private DonationRequest findRequest(Long id) {

        return requestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Request not found"));
    }

    private void checkPending(DonationRequest request) {

        if (request.getRequestStatus() != RequestStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only pending requests can be processed");
        }
    }

    private void checkDonationOwner(Donation donation, User user) {

        if (!donation.getDonor().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot manage requests for another donor's donation");
        }
    }

    private void checkRecipient(User user) {

        if (user.getRole() != Role.RECIPIENT) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only recipients can request donations");
        }
    }

    private void checkDonor(User user) {

        if (user.getRole() != Role.DONOR) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only donors can manage received requests");
        }
    }

    private RequestResponse mapToResponse(DonationRequest request) {

        Donation donation = request.getDonation();

        return new RequestResponse(
                request.getId(),
                donation.getId(),
                donation.getFoodName(),
                request.getRecipient().getId(),
                request.getRecipient().getName(),
                donation.getDonor().getId(),
                donation.getDonor().getName(),
                request.getRequestStatus(),
                request.getRequestedAt(),
                request.getApprovedAt(),
                request.getVolunteer() != null ? request.getVolunteer().getId() : null,
                request.getVolunteer() != null ? request.getVolunteer().getName() : null,
                request.getAssignmentStatus()
        );
    }
}