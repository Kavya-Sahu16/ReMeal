package com.kavya.remeal.user.dto;

import com.kavya.remeal.user.Role;

public record ProfileResponse(
        Long id,
        String name,
        String email,
        String contactNumber,
        Role role,
        String organisationName,
        String city,
        String address
) {
}