package com.innowise.authservice.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RefreshRequest(
        @NotBlank
        @Size(max = 2048)
        String refreshToken
) {
}
