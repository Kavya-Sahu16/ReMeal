package com.kavya.remeal.donation.dto;

import com.kavya.remeal.donation.DonationCategory;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DonationRequest(

        @NotBlank(message = "Food name is required")
        @Size(max = 100)
        String foodName,

        @NotNull(message = "Category is required")
        DonationCategory category,

        @Size(max = 1000)
        String description,

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.0", inclusive = false,
                message = "Quantity must be greater than zero")
        @Digits(integer = 8, fraction = 2)
        BigDecimal quantity,

        @NotBlank(message = "Quantity unit is required")
        @Size(max = 30)
        String quantityUnit,

        @NotBlank(message = "Pickup address is required")
        @Size(max = 255)
        String pickupAddress,

        @NotBlank(message = "City is required")
        @Size(max = 100)
        String city,

        @NotNull(message = "Preparation date and time are required")
        LocalDateTime preparationDateTime,

        @NotNull(message = "Expiry date and time are required")
        LocalDateTime expiryDateTime
) {
}