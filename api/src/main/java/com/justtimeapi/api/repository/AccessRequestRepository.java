package com.justtimeapi.api.repository;

import com.justtimeapi.api.dto.request.RequestAccessBody;
import com.justtimeapi.api.dto.response.AccessRequestResponse;
import com.justtimeapi.api.dto.response.ResourceSummaryResponse;
import com.justtimeapi.api.enums.AccessRequestStatus;
import com.justtimeapi.api.models.AccessRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AccessRequestRepository {
    private final JdbcTemplate jdbc;

    private final RowMapper<AccessRequest> mapper = (ResultSet rs, int rowNum) -> {
        return AccessRequest.builder()
                .id(UUID.fromString(rs.getString("id")))
                .userId(UUID.fromString(rs.getString("user_id")))
                .resourceId(UUID.fromString(rs.getString("resource_id")))
                .reason(rs.getString("reason"))
                .status(AccessRequestStatus.valueOf(rs.getString("status")))
                .requestedHours(rs.getInt("requested_hours"))
                .requestAt(rs.getTimestamp("requested_at").toLocalDateTime())
                .reviewedAt(rs.getTimestamp("reviewed_at") != null ? rs.getTimestamp("reviewed_at").toLocalDateTime() : null)
                .reviewedBy(rs.getString("reviewed_by") != null ? UUID.fromString(rs.getString("reviewed_by")) : null)
                .build();
    };

    private final RowMapper<AccessRequestResponse> accessRequestResponseMapper = (rs, rowNum) ->
            new AccessRequestResponse(
                    UUID.fromString(rs.getString("id")),
                    rs.getString("status"),
                    rs.getString("reason"),
                    rs.getInt("requested_hours"),
                    rs.getTimestamp("requested_at").toLocalDateTime(),
                    rs.getTimestamp("reviewed_at") != null ? rs.getTimestamp("reviewed_at").toLocalDateTime() : null,
                    new ResourceSummaryResponse(
                            UUID.fromString(rs.getString("resource_id")),
                            rs.getString("resource_name"),
                            rs.getString("resource_type"),
                            rs.getString("resource_status")
                    )
            );


    public AccessRequest save(RequestAccessBody accessRequest) {
        String sql = """
            INSERT INTO access_requests (user_id, resource_id, reason, requested_hours)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (user_id, resource_id) DO NOTHING
            RETURNING *;
        """;

        return jdbc.queryForObject(sql, mapper,
                accessRequest.userId(),
                accessRequest.resourceId(),
                accessRequest.reason(),
                accessRequest.requestedHours()
        );
    }

    public List<AccessRequest> findAll() {
        return jdbc.query("SELECT * FROM access_requests ORDER BY requested_at DESC", mapper);
    }

    public List<AccessRequest> findByUserId(UUID userId) {
        String sql = """
                SELECT * FROM access_requests
                WHERE user_id = ?
                ORDER BY requested_at DESC
                """;

        return jdbc.query(sql, mapper, userId);
    }

    public Optional<AccessRequest> findById(UUID id) {
        List<AccessRequest> result = jdbc.query("SELECT * FROM access_requests WHERE id=?", mapper, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
    }

    public Optional<AccessRequestResponse> findByIdToAccessRequestResponse(UUID id) {
        String sql = """
                SELECT
                    ar.id,
                    ar.resource_id,
                    ar.reason,
                    ar.status,
                    ar.requested_hours,
                    ar.requested_at,
                    ar.reviewed_at,
                
                    r.name AS resource_name,
                    rt.code AS resource_type,
                    r.status AS resource_status
                
                FROM access_requests ar
                JOIN resources r ON r.id = ar.resource_id
                JOIN resource_types rt ON rt.id = r.type_id
                WHERE ar.id = ?;
                """;

        return jdbc.query(sql, accessRequestResponseMapper, id).stream().findFirst();
    }

    public void updateStatus(UUID id, AccessRequestStatus status, UUID adminId) {
        String sql = """
            UPDATE access_requests
            SET status=?, reviewed_at=NOW(), reviewed_by=?
            WHERE id=?
        """;

        jdbc.update(sql, status.toString(), adminId, id);
    }
}
