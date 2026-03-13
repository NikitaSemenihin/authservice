package com.innowise.authservice.service;

import com.innowise.authservice.entity.dto.LoginRequest;
import com.innowise.authservice.entity.dto.RegisterRequest;
import com.innowise.authservice.entity.dto.RoleUpdateRequest;
import com.innowise.authservice.entity.dto.TokenResponse;
import com.innowise.authservice.entity.dto.TokenValidationResponse;

public interface AuthService {
    TokenResponse register(RegisterRequest request);
    TokenResponse login(LoginRequest request);
    TokenValidationResponse validate(String token);
    TokenResponse refresh(String refreshToken);
    void updateRole(Long userId, RoleUpdateRequest request);
}
