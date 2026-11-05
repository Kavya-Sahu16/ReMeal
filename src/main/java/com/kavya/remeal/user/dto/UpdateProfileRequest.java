package com.kavya.remeal.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 100)
        String name,

        @NotBlank(message = "Contact number is required")
        @Pattern(regexp = "^[0-9]{10}$",
                message = "Contact number must contain exactly 10 digits")
        String contactNumber,

        @Size(max = 150)
        String organisationName,

        @NotBlank(message = "City is required")
        @Size(max = 100)
        String city,

        @NotBlank(message = "Address is required")
        @Size(max = 255)
        String address
) {
}