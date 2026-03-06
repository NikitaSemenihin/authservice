package com.innowise.authservice.entity.dto;

import com.innowise.authservice.entity.Role;

public record TokenValidationResponse(
        boolean valid,
        Long userId,
        Role role
) {
}
