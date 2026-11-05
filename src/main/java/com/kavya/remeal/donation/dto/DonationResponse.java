package com.kavya.remeal.donation.dto;

import com.kavya.remeal.donation.DonationCategory;
import com.kavya.remeal.donation.DonationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DonationResponse(
        Long id,
        Long donorId,
        String foodName,
        DonationCategory category,
        String description,
        BigDecimal quantity,
        String quantityUnit,
        String pickupAddress,
        String city,
        LocalDateTime preparationDateTime,
        LocalDateTime expiryDateTime,
        DonationStatus status,
        LocalDateTime createdAt
) {
}