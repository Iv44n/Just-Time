package com.justtimeapi.api.dto.response;

import com.justtimeapi.api.models.ResourceType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ResourceClientResponse(
        UUID id,
        String name,
        ResourceType type,
        String status,
        UUID createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String requestStatus,
        String requestId
) {}
