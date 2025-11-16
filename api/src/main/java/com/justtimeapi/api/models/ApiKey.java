package com.justtimeapi.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ApiKey {
    private UUID id;
    private UUID userId;
    private UUID resourceId;
    private String keyPrefix;
    private String keyHash;
    private LocalDateTime expiresAt;
    private boolean revoked;
    private boolean revealed;
    private LocalDateTime createdAt;
}
