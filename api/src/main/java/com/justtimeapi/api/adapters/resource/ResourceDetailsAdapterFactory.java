package com.justtimeapi.api.adapters.resource;

import com.justtimeapi.api.dto.request.CreateResourceRequest;

public class ResourceDetailsAdapterFactory {
    public static ResourceDetailsAdapter create(CreateResourceRequest.Details details) {
        if (details.connectionUrl() != null && !details.connectionUrl().isBlank()) {
            return new ConnectionUrlAdapter(details.connectionUrl());
        } else {
            return new DetailedAdapter(details);
        }
    }
}