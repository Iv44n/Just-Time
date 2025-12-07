package com.justtimeapi.api.dto.response;

import java.util.List;
import java.util.Map;

public record QueryResultResponse(
        List<String> columns,
        List<Map<String, Object>> rows,
        Integer updateCount
) {}
