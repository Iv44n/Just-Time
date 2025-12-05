package com.justtimeapi.api.adapters.resource;

import com.justtimeapi.api.dto.request.CreateResourceRequest;
import com.justtimeapi.api.models.ResourceDbDetails;

import java.util.UUID;

public record DetailedAdapter(CreateResourceRequest.Details details) implements ResourceDetailsAdapter {
    @Override
    public ResourceDbDetails toResourceDbDetails(UUID resourceId) {
        if (details.engine() == null || details.engine().isBlank()) {
            throw new IllegalArgumentException("Engine is required");
        }
        if (details.host() == null || details.host().isBlank()) {
            throw new IllegalArgumentException("Host is required");
        }
        if (details.database() == null || details.database().isBlank()) {
            throw new IllegalArgumentException("Database name is required");
        }
        if (details.username() == null || details.username().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (details.password() == null || details.password().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        return ResourceDbDetails.builder()
                .resourceId(resourceId)
                .engine(details.engine())
                .host(details.host())
                .port(details.port() != null ? details.port().intValue() : null)
                .databaseName(details.database())
                .username(details.username())
                .encryptedPassword(encrypt(details.password()))
                .build();
    }

    private String encrypt(String password) {
        // TODO: Implementar cifrado real
        return password;
    }
}
