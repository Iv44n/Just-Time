package com.justtimeapi.api.utils;

import com.justtimeapi.api.dto.response.QueryResultResponse;
import com.justtimeapi.api.interfaces.QueryExecute;
import com.justtimeapi.api.services.QueryExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class QueryProxy implements QueryExecute {
    private final QueryExecutionService realQueryExecutionService;

    @Override
    public QueryResultResponse execute(UUID accessRequestId, UUID resourceId, String sql) {
        // validar la query (sql)
        return realQueryExecutionService.execute(accessRequestId, resourceId, sql);
    }
}
