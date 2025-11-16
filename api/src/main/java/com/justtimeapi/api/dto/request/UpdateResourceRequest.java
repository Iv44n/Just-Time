package com.justtimeapi.api.dto.request;


import com.justtimeapi.api.enums.ResourceType;

public record UpdateResourceRequest(
        String name,
        ResourceType type,
        String connectionUrl,
        String username,
        String password,
        String status
) {}