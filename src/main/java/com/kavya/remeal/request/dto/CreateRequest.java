package com.kavya.remeal.request.dto;

import jakarta.validation.constraints.NotNull;

public record CreateRequest(

        @NotNull(message = "Donation ID is required")
        Long donationId

) {
}