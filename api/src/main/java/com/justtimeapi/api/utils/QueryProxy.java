package com.justtimeapi.api.utils;

import com.justtimeapi.api.dto.response.QueryResultResponse;
import com.justtimeapi.api.exception.exceptions.ReadOnlyQueryException;
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
        String normalized = sql.trim().toUpperCase();

        if (!normalized.startsWith("SELECT")) {
            throw new ReadOnlyQueryException("Solo se permiten consultas de lectura (SELECT).");
        }

        String[] forbidden = {
                "INSERT ", "UPDATE ", "DELETE ", "DROP ", "ALTER ",
                "TRUNCATE ", "CREATE ", "REPLACE ", "EXEC ", "MERGE "
        };

        for (String keyword : forbidden) {
            if (normalized.contains(keyword)) {
                throw new ReadOnlyQueryException("La consulta contiene operaciones no permitidas: " + keyword.trim());
            }
        }

        return realQueryExecutionService.execute(accessRequestId, resourceId, sql);
    }
}
