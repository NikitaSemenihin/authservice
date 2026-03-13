package com.innowise.authservice.entity.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
