package com.justtimeapi.api.repository;

import com.justtimeapi.api.dto.request.RequestAccessBody;
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

    public void updateStatus(UUID id, AccessRequestStatus status, UUID adminId) {
        String sql = """
            UPDATE access_requests
            SET status=?, reviewed_at=NOW(), reviewed_by=?
            WHERE id=?
        """;

        jdbc.update(sql, status.toString(), adminId, id);
    }
}
