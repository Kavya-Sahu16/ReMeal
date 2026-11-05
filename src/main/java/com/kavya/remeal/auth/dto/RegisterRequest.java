package com.kavya.remeal.auth.dto;

import com.kavya.remeal.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

    @NotBlank(message = "Name is required")
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must contain at least 8 characters")
    String password,

    @NotBlank(message = "Contact number is required")
    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Contact number must contain exactly 10 digits"
    )
    String contactNumber,

    @NotNull(message = "Role is required")
    Role role,

    String organisationName,

    @NotBlank(message = "City is required")
    String city,

    @NotBlank(message = "Address is required")
    String address

) {}