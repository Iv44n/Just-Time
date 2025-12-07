package com.justtimeapi.api.repository;

import com.justtimeapi.api.models.ResourceDbDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ResourceDbDetailsRepository {
    private final JdbcTemplate jdbc;

    private final RowMapper<ResourceDbDetails> mapper = (ResultSet rs, int rowNum) -> {
        return ResourceDbDetails.builder()
                .resourceId(UUID.fromString(rs.getString("resource_id")))
                .engine(rs.getString("engine"))
                .host(rs.getString("host"))
                .port(rs.getInt("port"))
                .databaseName(rs.getString("db_name"))
                .username(rs.getString("username"))
                .encryptedPassword(rs.getString("encrypted_password"))
                .params(rs.getString("params"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    };

    public void save(ResourceDbDetails resourceDbDetails) {
        String sql = """
                INSERT INTO resource_db_details (
                    resource_id,
                    engine, host, port, db_name,
                    username, encrypted_password,
                    params
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING *;
                """;

        jdbc.queryForObject(sql, mapper,
                resourceDbDetails.getResourceId(),
                resourceDbDetails.getEngine(),
                resourceDbDetails.getHost(),
                resourceDbDetails.getPort(),
                resourceDbDetails.getDatabaseName(),
                resourceDbDetails.getUsername(),
                resourceDbDetails.getEncryptedPassword(),
                resourceDbDetails.getParams());
    }

    public Optional<ResourceDbDetails> findByResourceId(UUID resourceId){
        String sql = "SELECT * FROM resource_db_details WHERE resource_id = ?";

        return jdbc.query(sql, mapper, resourceId).stream().findFirst();
    }
}
