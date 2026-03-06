package com.innowise.authservice.entity.dto;

import java.time.LocalDate;

public record UserCreateRequest(
        String name,
        String surname,
        String email,
        LocalDate birthDate
) {
}
