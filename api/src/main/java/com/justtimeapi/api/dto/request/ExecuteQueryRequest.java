package com.justtimeapi.api.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record ExecuteQueryRequest(
        @NotBlank
        String sql,
        UUID accessRequestId
) {
}
