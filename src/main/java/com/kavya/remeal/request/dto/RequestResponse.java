package com.kavya.remeal.request.dto;

import com.kavya.remeal.request.AssignmentStatus;
import com.kavya.remeal.request.RequestStatus;
import com.kavya.remeal.request.DeliveryStatus;
import java.time.LocalDateTime;

public record RequestResponse(
        Long id,
        Long donationId,
        String foodName,
        Long recipientId,
        String recipientName,
        Long donorId,
        String donorName,
        RequestStatus requestStatus,
        LocalDateTime requestedAt,
        LocalDateTime approvedAt,
        Long volunteerId,
        String volunteerName,
        AssignmentStatus assignmentStatus,
        DeliveryStatus deliveryStatus,
        LocalDateTime pickedUpAt,
        LocalDateTime deliveredAt,
        LocalDateTime confirmedAt
    ) {
}