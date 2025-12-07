package com.justtimeapi.api.services;

import com.justtimeapi.api.dto.response.QueryResultResponse;
import com.justtimeapi.api.interfaces.QueryExecute;
import com.justtimeapi.api.models.ResourceDbDetails;
import com.justtimeapi.api.repository.ResourceDbDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class QueryExecutionService implements QueryExecute {
    private final DynamicConnectionManager connectionManager;
    private final ResourceDbDetailsRepository resourceDbDetailsRepository;

    @Override
    public QueryResultResponse execute(UUID accessRequestId, UUID resourceId, String sql) {
        // validar que el access request este en aprovado y no este vencido
        ResourceDbDetails resourceDbDetails = resourceDbDetailsRepository.findByResourceId(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource with " + resourceId + " not found"));

        String url = getUrlFromResourceDbDetails(resourceDbDetails);
        String driver = getDriverFromEngine(resourceDbDetails.getEngine());

        DataSource ds = connectionManager.getDataSource(url, resourceDbDetails.getUsername(), resourceDbDetails.getEncryptedPassword(), driver, resourceId);

        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {

            boolean isResultSet = stmt.execute(sql);

            if (isResultSet) {
                return mapResultSet(stmt.getResultSet());
            } else {
                int updateCount = stmt.getUpdateCount();
                return new QueryResultResponse(null, null, updateCount);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getUrlFromResourceDbDetails(ResourceDbDetails d) {
        if (d == null) {
            throw new IllegalArgumentException("ResourceDbDetails cannot be null");
        }

        String engine = d.getEngine().toLowerCase().trim();
        String host = d.getHost();
        Integer port = d.getPort();
        String db = d.getDatabaseName();
        String params = d.getParams(); // texto crudo o null

        StringBuilder url = new StringBuilder();

        switch (engine) {

            /* ------------------- POSTGRESQL ------------------- */
            case "postgres":
            case "postgresql":
                url.append("jdbc:postgresql://")
                        .append(host);

                if (port != null) {
                    url.append(":").append(port);
                }

                url.append("/").append(db);
                break;

            /* ------------------- MYSQL ------------------- */
            case "mysql":
                url.append("jdbc:mysql://")
                        .append(host);

                if (port != null) {
                    url.append(":").append(port);
                }

                url.append("/").append(db);
                break;

            /* ------------------- MARIADB ------------------- */
            case "mariadb":
                url.append("jdbc:mariadb://")
                        .append(host);

                if (port != null) {
                    url.append(":").append(port);
                }

                url.append("/").append(db);
                break;

            default:
                throw new IllegalArgumentException("Unsupported engine: " + engine);
        }

        /* ----------- Agregar parámetros si existen ----------- */
        if (params != null && !params.isBlank()) {
            // Si params NO comienza con "?", lo agregamos
            if (!params.startsWith("?")) {
                url.append("?");
            }
            url.append(params);
        }

        return url.toString();
    }

    private String getDriverFromEngine(String engine) {
        if (engine == null || engine.isBlank()) {
            throw new IllegalArgumentException("Engine cannot be null or empty");
        }

        return switch (engine.toLowerCase().trim()) {
            case "postgres", "postgresql" -> "org.postgresql.Driver";
            case "mysql" -> "com.mysql.cj.jdbc.Driver";
            case "mariadb" -> "org.mariadb.jdbc.Driver";

            // Puedes agregar más según soporte:
            case "sqlserver" -> "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case "oracle" -> "oracle.jdbc.driver.OracleDriver";
            default -> throw new IllegalArgumentException("Unsupported database engine: " + engine);
        };
    }

    private QueryResultResponse mapResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        List<String> columns = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            columns.add(meta.getColumnName(i));
        }

        List<Map<String, Object>> rows = new ArrayList<>();

        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                Object value = rs.getObject(i);
                row.put(meta.getColumnName(i), value);
            }
            rows.add(row);
        }

        return new QueryResultResponse(columns, rows, null);
    }

}
