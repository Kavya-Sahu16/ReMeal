package com.kavya.remeal.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationRequestRepository
        extends JpaRepository<DonationRequest, Long> {

    List<DonationRequest> findAllByRecipientIdOrderByRequestedAtDesc(
            Long recipientId);

    List<DonationRequest> findAllByDonation_Donor_IdOrderByRequestedAtDesc(
            Long donorId);

    List<DonationRequest> findAllByDonationIdAndRequestStatus(
            Long donationId, RequestStatus requestStatus);

    boolean existsByDonationIdAndRecipientIdAndRequestStatusIn(
            Long donationId,
            Long recipientId,
            List<RequestStatus> statuses);
}