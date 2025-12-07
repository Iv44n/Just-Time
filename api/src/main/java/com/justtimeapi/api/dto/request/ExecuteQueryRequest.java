package com.justtimeapi.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record executeQueryRequest(
        @NotBlank
        String sql
) {
}
