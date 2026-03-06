package com.innowise.authservice.client.impl;

import com.innowise.authservice.client.UserServiceClient;
import com.innowise.authservice.entity.dto.UserCreateRequest;
import com.innowise.authservice.entity.dto.UserResponseDto;
import com.innowise.authservice.exeption.UserAlreadyExistsException;
import com.innowise.authservice.exeption.UserServiceClientException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Service
public class UserServiceClientImpl implements UserServiceClient {

    private static final String SERVICE_NAME_HEADER = "X-Service-Name";
    private static final String SERVICE_NAME = "authservice";
    private final RestClient restClient;

    public UserServiceClientImpl(RestClient userServiceRestClient) {
        this.restClient = userServiceRestClient;
    }

    @Override
    public UserResponseDto createUser(UserCreateRequest request) {
        try {
            UserResponseDto body = restClient.post()
                    .uri("/api/users")
                    .header(SERVICE_NAME_HEADER, SERVICE_NAME)
                    .body(request)
                    .retrieve()
                    .body(UserResponseDto.class);

            if (body == null || body.id() == null) {
                throw new UserServiceClientException("User service returned empty response for user creation");
            }

            return body;
        } catch (ResourceAccessException exception) {
            throw new UserServiceClientException("User service is unavailable", exception);
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().value() == 409) {
                throw new UserAlreadyExistsException("User with this email already exists", exception);
            }
            throw new UserServiceClientException(
                    "User service response error: " + exception.getStatusCode(),
                    exception
            );
        } catch (RestClientException exception) {
            throw new UserServiceClientException("Failed to call user service", exception);
        }
    }

    @Override
    public void deleteUser(Long userId) {
        try {
            restClient.delete()
                    .uri("/api/users/internal/{id}", userId)
                    .header(SERVICE_NAME_HEADER, SERVICE_NAME)
                    .retrieve()
                    .toBodilessEntity();
        } catch (ResourceAccessException exception) {
            throw new UserServiceClientException("User service is unavailable during rollback", exception);
        } catch (RestClientResponseException exception) {
            throw new UserServiceClientException(
                    "User service response error during rollback: " + exception.getStatusCode(),
                    exception
            );
        } catch (RestClientException exception) {
            throw new UserServiceClientException("Failed to call user service during rollback", exception);
        }
    }
}
