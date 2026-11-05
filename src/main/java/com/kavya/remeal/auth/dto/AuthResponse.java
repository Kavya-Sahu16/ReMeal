package com.kavya.remeal.auth.dto;

import com.kavya.remeal.user.Role;

public record AuthResponse(
    String token,
    String tokenType,
    Long userId,
    String name,
    String email,
    Role role
) {}