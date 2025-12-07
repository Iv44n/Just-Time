package com.justtimeapi.api.repository;

import com.justtimeapi.api.dto.request.UpdateResourceRequest;
import com.justtimeapi.api.dto.response.ResourceClientResponse;
import com.justtimeapi.api.models.Resource;
import com.justtimeapi.api.models.ResourceType;
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

    private final RowMapper<Resource> mapperToGet = (ResultSet rs, int rowNum) -> {
        String createdByStr = rs.getString("created_by");
        UUID createdById = createdByStr != null ? UUID.fromString(createdByStr) : null;

        ResourceType type = ResourceType.builder()
                .id(rs.getInt("type_id"))
                .code(rs.getString("type_code"))
                .description(rs.getString("type_description"))
                .build();

        return Resource.builder()
                .id(UUID.fromString(rs.getString("id")))
                .name(rs.getString("name"))
                .type(type)
                .status(rs.getString("status"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .createdBy(createdById)
                .build();
    };

    private final RowMapper<Resource> mapperToSave = (ResultSet rs, int rowNum) -> {
        String createdByStr = rs.getString("created_by");
        UUID createdById = createdByStr != null ? UUID.fromString(createdByStr) : null;

        return Resource.builder()
                .id(UUID.fromString(rs.getString("id")))
                .name(rs.getString("name"))
                .status(rs.getString("status"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .createdBy(createdById)
                .build();
    };

    private final RowMapper<ResourceClientResponse> mapperToGetWithRequestStatus = (ResultSet rs, int rowNum) -> {
        String createdByStr = rs.getString("created_by");
        UUID createdById = createdByStr != null ? UUID.fromString(createdByStr) : null;

        ResourceType type = ResourceType.builder()
                .id(rs.getInt("type_id"))
                .code(rs.getString("type_code"))
                .description(rs.getString("type_description"))
                .build();

        Resource resource =  Resource.builder()
                .id(UUID.fromString(rs.getString("id")))
                .name(rs.getString("name"))
                .type(type)
                .status(rs.getString("status"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .createdBy(createdById)
                .build();

        return ResourceClientResponse.builder()
                .id(resource.getId())
                .name(resource.getName())
                .type(resource.getType())
                .status(resource.getStatus())
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt())
                .createdBy(resource.getCreatedBy())
                .requestStatus(rs.getString("request_status"))
                .requestId(rs.getString("request_id"))
                .build();
    };

    public List<Resource> findAll() {
        String sql = """
                SELECT
                    r.*,
                    rt.id AS type_id,
                    rt.code AS type_code,
                    rt.description AS type_description
                FROM resources r
                JOIN resource_types rt ON r.type_id = rt.id
                ORDER BY r.created_at DESC
                """;

        return jdbc.query(sql, mapperToGet);
    }

    public List<ResourceClientResponse> findResourcesForUser(UUID userId){
        String sql = """
                SELECT
                    r.*,
                    rt.id AS type_id,
                    rt.code AS type_code,
                    rt.description AS type_description,
                    ar.status AS request_status,
                    ar.id as request_id
                FROM resources r
                JOIN resource_types rt ON r.type_id = rt.id
                LEFT JOIN access_requests ar
                ON ar.resource_id = r.id AND ar.user_id = ?
                WHERE r.status = 'ACTIVE'
                ORDER BY r.created_at DESC
                """;

        return jdbc.query(sql, mapperToGetWithRequestStatus, userId);
    }

    public Optional<Resource> findById(UUID id) {
        String sql = """
                SELECT
                    r.*,
                    rt.id AS type_id,
                    rt.code AS type_code,
                    rt.description AS type_description
                FROM resources r
                JOIN resource_types rt ON r.type_id = rt.id
                WHERE r.id = ?
                """;

        return jdbc.query(sql, mapperToGet, id).stream().findFirst();
    }

    public Resource save(Resource resource) {
        String sql = """
                INSERT INTO resources (name, type_id, created_by)
                VALUES (?, ?, ?)
                RETURNING *
                """;

        Resource saved = jdbc.queryForObject(sql, mapperToSave,
                resource.getName(),
                resource.getType().getId(),
                resource.getCreatedBy()
        );

        if (saved == null) {
            throw new IllegalStateException("INSERT returned null Resource");
        }

        saved.setType(resource.getType());
        return saved;
    }

    public Optional<UUID> delete(UUID id) {
        String sql = "DELETE FROM resources WHERE id = ? RETURNING id";
        return jdbc.query(
                sql,
                (rs, rowNum) -> UUID.fromString(rs.getString("id")),
                id
        ).stream().findFirst();
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

        return jdbc.queryForObject(sql.toString(), mapperToGet, params.toArray());
    }
}
