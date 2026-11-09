package com.kavya.remeal.dashboard;

import com.kavya.remeal.dashboard.dto.*;
import com.kavya.remeal.donation.*;
import com.kavya.remeal.request.*;
import com.kavya.remeal.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DonationRepository donationRepository;
    private final DonationRequestRepository requestRepository;

    public DonorDashboardResponse getDonorDashboard(User user) {

        checkRole(user, Role.DONOR);

        Long id = user.getId();

        return new DonorDashboardResponse(
                donationRepository.countByDonor_Id(id),

                donationRepository.countByDonor_IdAndStatus(
                        id, DonationStatus.AVAILABLE),

                requestRepository.countByDonation_Donor_IdAndRequestStatus(
                        id, RequestStatus.PENDING),

                requestRepository.countByDonation_Donor_IdAndDeliveryStatus(
                        id, DeliveryStatus.CONFIRMED)
        );
    }

    public RecipientDashboardResponse getRecipientDashboard(User user) {

        checkRole(user, Role.RECIPIENT);

        Long id = user.getId();

        return new RecipientDashboardResponse(
                requestRepository.countByRecipient_Id(id),

                requestRepository.countByRecipient_IdAndRequestStatus(
                        id, RequestStatus.PENDING),

                requestRepository.countByRecipient_IdAndRequestStatus(
                        id, RequestStatus.APPROVED),

                requestRepository.countByRecipient_IdAndDeliveryStatus(
                        id, DeliveryStatus.CONFIRMED)
        );
    }

    public VolunteerDashboardResponse getVolunteerDashboard(User user) {

        checkRole(user, Role.VOLUNTEER);

        Long id = user.getId();

        return new VolunteerDashboardResponse(
                requestRepository.countByVolunteer_Id(id),

                requestRepository.countByVolunteer_IdAndAssignmentStatus(
                        id, AssignmentStatus.ACCEPTED),

                requestRepository.countByVolunteer_IdAndDeliveryStatus(
                        id, DeliveryStatus.PICKED_UP),

                requestRepository.countByVolunteer_IdAndDeliveryStatus(
                        id, DeliveryStatus.CONFIRMED)
        );
    }

    private void checkRole(User user, Role requiredRole) {

        if (user.getRole() != requiredRole) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to access this dashboard"
            );
        }
    }
}