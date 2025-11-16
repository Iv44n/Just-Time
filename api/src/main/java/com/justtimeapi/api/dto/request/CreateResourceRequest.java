package com.justtimeapi.api.dto.request;

import com.justtimeapi.api.enums.ResourceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateResourceRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Type is required")
        ResourceType type,

        @NotBlank(message = "Connection URL is required")
        String connectionUrl,

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password
) {
}
