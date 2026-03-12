package com.innowise.authservice.service.impl;

import com.innowise.authservice.client.UserServiceClient;
import com.innowise.authservice.entity.Role;
import com.innowise.authservice.entity.UserCredential;
import com.innowise.authservice.entity.dto.LoginRequest;
import com.innowise.authservice.entity.dto.RegisterRequest;
import com.innowise.authservice.entity.dto.RoleUpdateRequest;
import com.innowise.authservice.entity.dto.TokenResponse;
import com.innowise.authservice.entity.dto.TokenValidationResponse;
import com.innowise.authservice.entity.dto.UserCreateRequest;
import com.innowise.authservice.entity.dto.UserResponseDto;
import com.innowise.authservice.exception.InvalidCredentialsException;
import com.innowise.authservice.exception.RegistrationOrchestrationException;
import com.innowise.authservice.exception.UserInactiveException;
import com.innowise.authservice.exception.UserNotFoundException;
import com.innowise.authservice.repository.UserCredentialRepository;
import com.innowise.authservice.security.JwtService;
import com.innowise.authservice.service.AuthService;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserCredentialRepository repository;
    private final UserServiceClient userServiceClient;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserCredentialRepository repository,
            UserServiceClient userServiceClient,
            JwtService jwtService,
            PasswordEncoder encoder
    ) {
        this.repository = repository;
        this.userServiceClient = userServiceClient;
        this.jwtService = jwtService;
        this.encoder = encoder;
    }


    @Override
    public TokenResponse register(RegisterRequest request) {
        UserResponseDto createdUser = userServiceClient.createUser(new UserCreateRequest(
                request.name(),
                request.surname(),
                request.email(),
                request.birthDate()
        ));


        // Credentials depend on the userId returned by User Service,
        // so any failure after user creation must trigger compensating rollback.
        try {
            String hash = encoder.encode(request.password());

            UserCredential credential = new UserCredential();
            credential.setLogin(request.login());
            credential.setUserId(createdUser.id());
            credential.setPasswordHash(hash);
            credential.setRole(Role.USER);

            UserCredential savedCredential = repository.save(credential);
            return new TokenResponse(
                    jwtService.generateAccessToken(savedCredential),
                    jwtService.generateRefreshToken(savedCredential)
            );
        } catch (RuntimeException exception) {
            rollbackUserCreation(createdUser.id(), exception);
            throw exception;
        }
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        UserCredential user = repository.findByLogin(request.login())
                .orElseThrow(UserNotFoundException::new);

        if (!user.isActive()) {
            throw new UserInactiveException();
        }
        if (!encoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return new TokenResponse(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user)
        );
    }

    @Override
    public TokenValidationResponse validate(String token) {
        Claims claims = jwtService.parse(token);
        return new TokenValidationResponse(
                true,
                claims.get("userId", Long.class),
                Role.valueOf(claims.get("role", String.class))
        );
    }

    @Override
    public TokenResponse refresh(String refreshToken) {
        Claims claims = jwtService.parse(refreshToken);

        String login = claims.getSubject();
        UserCredential user = repository.findByLogin(login)
                .orElseThrow(UserNotFoundException::new);

        if (!user.isActive()) {
            throw new UserInactiveException();
        }
        return new TokenResponse(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user)
        );
    }

    @Override
    public void updateRole(Long userId, RoleUpdateRequest request) {
        UserCredential user = repository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        user.setRole(request.role());
        repository.save(user);
    }

    private void rollbackUserCreation(Long userId, RuntimeException initialException) {
        try {
            userServiceClient.deleteUser(userId);
        } catch (RuntimeException rollbackException) {
            rollbackException.addSuppressed(initialException);
            throw new RegistrationOrchestrationException(
                    "Registration failed and rollback to user service also failed",
                    rollbackException
            );
        }
    }
}
