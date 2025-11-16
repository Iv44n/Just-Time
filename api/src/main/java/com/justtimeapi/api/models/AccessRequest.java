package com.justtimeapi.api.models;

import com.justtimeapi.api.enums.AccessRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class AccessRequest {
    private UUID id;
    private UUID userId;
    private UUID resourceId;
    private String reason;
    private AccessRequestStatus status;
    private Integer requestedHours;
    private LocalDateTime requestAt;
    private LocalDateTime reviewedAt;
    private UUID reviewedBy;
}
