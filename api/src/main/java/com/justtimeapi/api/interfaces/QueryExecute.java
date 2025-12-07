package com.justtimeapi.api.interfaces;

import com.justtimeapi.api.dto.response.QueryResultResponse;

import java.util.UUID;

public interface QueryExecute {
    QueryResultResponse execute(UUID accessRequestId, UUID resourceId, String sql);
}
