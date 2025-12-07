package com.justtimeapi.api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccessRequestResponse(
        UUID id,
        String status,
        String reason,
        Integer requestedHours,
        LocalDateTime requestedAt,
        LocalDateTime reviewedAt,
        ResourceSummaryResponse resource
) {}

