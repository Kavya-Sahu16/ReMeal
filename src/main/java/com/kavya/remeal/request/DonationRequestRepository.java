package com.kavya.remeal.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationRequestRepository extends JpaRepository<DonationRequest, Long> {

    List<DonationRequest> findAllByRecipientIdOrderByRequestedAtDesc(Long recipientId);

    List<DonationRequest> findAllByDonation_Donor_IdOrderByRequestedAtDesc(Long donorId);

    List<DonationRequest> findAllByDonationIdAndRequestStatus(Long donationId, RequestStatus requestStatus);

    boolean existsByDonationIdAndRecipientIdAndRequestStatusIn(Long donationId, Long recipientId, List<RequestStatus> statuses);

    List<DonationRequest> findAllByVolunteerIdOrderByRequestedAtDesc(Long volunteerId);

    // DONOR
long countByDonation_Donor_IdAndRequestStatus(
        Long donorId,
        RequestStatus status
);

long countByDonation_Donor_IdAndDeliveryStatus(
        Long donorId,
        DeliveryStatus status
);

// RECIPIENT
long countByRecipient_Id(Long recipientId);

long countByRecipient_IdAndRequestStatus(
        Long recipientId,
        RequestStatus status
);

long countByRecipient_IdAndDeliveryStatus(
        Long recipientId,
        DeliveryStatus status
);

// VOLUNTEER
long countByVolunteer_Id(Long volunteerId);

long countByVolunteer_IdAndAssignmentStatus(
        Long volunteerId,
        AssignmentStatus status
);

long countByVolunteer_IdAndDeliveryStatus(
        Long volunteerId,
        DeliveryStatus status
);
}