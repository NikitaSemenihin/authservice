package com.innowise.authservice.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank
        @Size(min = 1, max = 100)
        String name,
        @NotBlank
        @Size(min = 1, max = 100)
        String surname,
        @Email
        @NotBlank
        @Size(max = 255)
        String email,
        @NotNull
        LocalDate birthDate,
        @NotBlank
        @Size(min = 3, max = 50)
        String login,
        @NotBlank
        @Size(min = 8, max = 72)
        String password
) {
}
