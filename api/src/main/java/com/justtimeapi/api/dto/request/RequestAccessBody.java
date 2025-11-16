package com.justtimeapi.api.dto.request;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record RequestAccessBody(
        @NotNull(message = "User ID cannot be null")
        UUID userId,

        @NotNull(message = "Resource ID cannot be null")
        UUID resourceId,

        @NotBlank(message = "Reason cannot be blank")
        @Size(max = 500, message = "Reason must not exceed 500 characters")
        String reason,

        @Min(value = 1, message = "Requested hours must be at least 1")
        @NotNull(message = "Requested hours is required")
        Integer requestedHours
) {
}
