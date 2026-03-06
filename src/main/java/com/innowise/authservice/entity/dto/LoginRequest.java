package com.innowise.authservice.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank
        @Size(min = 3, max = 50)
        String login,
        @NotBlank
        @Size(min = 8, max = 72)
        String password
) {
}
