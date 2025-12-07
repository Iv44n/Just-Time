package com.justtimeapi.api.dto.response;

import java.util.UUID;

public  record ResourceSummaryResponse(
        UUID id,
        String name,
        String type,
        String status
) {}
