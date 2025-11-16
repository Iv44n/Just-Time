package com.justtimeapi.api.repository;

import com.justtimeapi.api.dto.request.UpdateResourceRequest;
import com.justtimeapi.api.enums.ResourceType;
import com.justtimeapi.api.models.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ResourceRepository {
    private final JdbcTemplate jdbc;

    private final RowMapper<Resource> mapper = (ResultSet rs, int rowNum) -> {
        String createdByStr = rs.getString("created_by");
        UUID createdById = createdByStr != null ? UUID.fromString(createdByStr) : null;

        return Resource.builder()
                .id(UUID.fromString(rs.getString("id")))
                .name(rs.getString("name"))
                .type(ResourceType.valueOf(rs.getString("type")))
                .connectionUrl(rs.getString("connection_url"))
                .username(rs.getString("username"))
                .password(rs.getString("password"))
                .status(rs.getString("status"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .createdBy(createdById)
                .build();
    };

    public List<Resource> findAll() {
        String sql = "SELECT * FROM resources ORDER BY created_at DESC";
        return jdbc.query(sql, mapper);
    }

    public Optional<Resource> findById(UUID id) {
        String sql = "SELECT * FROM resources WHERE id = ?";
        List<Resource> results = jdbc.query(sql, mapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    public Resource save(Resource resource) {
        String sql = """
                INSERT INTO resources (name, type, connection_url, username, password)
                VALUES (?, ?, ?, ?, ?)
                RETURNING *
                """;

        return jdbc.queryForObject(sql, mapper,
                resource.getName(),
                resource.getType().toString(),
                resource.getConnectionUrl(),
                resource.getUsername(),
                resource.getPassword()
        );
    }

    public Optional<UUID> delete(UUID id) {
        String sql = "DELETE FROM resources WHERE id = ? RETURNING id";
        return Optional.ofNullable(jdbc.queryForObject(sql, UUID.class, id));
    }

    public Resource updateResourceById(UUID resourceId, UpdateResourceRequest req){
        StringBuilder sql = new StringBuilder("UPDATE resources SET ");
        List<Object> params = new ArrayList<>();

        if (req.name() != null) {
            sql.append("name = ?, ");
            params.add(req.name());
        }
        if (req.type() != null) {
            sql.append("type = ?, ");
            params.add(req.type().toString());
        }
        if (req.connectionUrl() != null) {
            sql.append("connection_url = ?, ");
            params.add(req.connectionUrl());
        }
        if (req.username() != null) {
            sql.append("username = ?, ");
            params.add(req.username());
        }
        if (req.password() != null) {
            sql.append("password = ?, ");
            params.add(req.password());
        }
        if (req.status() != null) {
            sql.append("status = ?, ");
            params.add(req.status());
        }

        if (params.isEmpty()) {
            throw new IllegalArgumentException("No fields provided for update");
        }

        sql.append("updated_at = NOW() WHERE id = ? RETURNING *");
        params.add(resourceId);

        return jdbc.queryForObject(sql.toString(), mapper, params.toArray());
    }
}
