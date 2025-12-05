package com.justtimeapi.api.adapters.resource;

import com.justtimeapi.api.models.ResourceDbDetails;

import java.util.UUID;

public interface ResourceDetailsAdapter {
    ResourceDbDetails toResourceDbDetails(UUID resourceId);
}
