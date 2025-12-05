package com.justtimeapi.api.dto.response;

import com.justtimeapi.api.enums.Roles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<Roles> roles
) {
}
