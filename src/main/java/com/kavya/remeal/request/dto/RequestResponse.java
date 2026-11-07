package com.kavya.remeal.request.dto;

import com.kavya.remeal.request.RequestStatus;

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
        LocalDateTime approvedAt
) {
}