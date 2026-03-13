package com.innowise.authservice.entity.dto;

import com.innowise.authservice.entity.Role;
import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequest(
        @NotNull
        Role role
) {
}
