package com.innowise.authservice.entity.dto;

import java.time.Instant;
import java.time.LocalDate;

public record UserResponseDto(
        Long id,
        String name,
        String surname,
        String email,
        boolean active,
        LocalDate birthDate,
        Instant createdAt,
        Instant updatedAt
) {
}
