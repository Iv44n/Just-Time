package com.justtimeapi.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AppSession {
    private UUID id;
    private UUID userId;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
