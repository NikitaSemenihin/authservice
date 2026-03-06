package com.innowise.authservice.controller;

import com.innowise.authservice.entity.dto.LoginRequest;
import com.innowise.authservice.entity.dto.RefreshRequest;
import com.innowise.authservice.entity.dto.RegisterRequest;
import com.innowise.authservice.entity.dto.RoleUpdateRequest;
import com.innowise.authservice.entity.dto.TokenResponse;
import com.innowise.authservice.entity.dto.TokenValidationRequest;
import com.innowise.authservice.entity.dto.TokenValidationResponse;
import com.innowise.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/validate")
    public TokenValidationResponse validate(@Valid @RequestBody TokenValidationRequest request) {
        return authService.validate(request.token());
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> statusUpdate(@PathVariable Long id, @Valid @RequestBody RoleUpdateRequest request) {
        authService.updateRole(id, request);
        return ResponseEntity.ok().build();
    }
}
