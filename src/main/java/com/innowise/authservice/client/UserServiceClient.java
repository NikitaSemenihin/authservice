package com.innowise.authservice.client;

import com.innowise.authservice.entity.dto.UserCreateRequest;
import com.innowise.authservice.entity.dto.UserResponseDto;

public interface UserServiceClient {
    UserResponseDto createUser(UserCreateRequest request);

    void deleteUser(Long userId);
}
