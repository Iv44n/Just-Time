package com.justtimeapi.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ResourceDbDetails {
    private UUID resourceId;
    private String engine;
    private String host;
    private Integer port;
    private String databaseName;
    private String username;
    private String encryptedPassword;
    private String params; // rest url
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
