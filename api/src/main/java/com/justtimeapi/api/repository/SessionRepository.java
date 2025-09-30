package com.justtimeapi.api.repository;

import com.justtimeapi.api.models.AppSession;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SessionRepository {
    private final JdbcTemplate jdbc;

    public AppSession createSession(UUID userId) {
        try {
            String sql = """
                    INSERT INTO app_sessions (id, user_id, expires_at)
                    VALUES (?, ?, ?)
                    RETURNING id, user_id, created_at, expires_at
                    """;

            UUID sessionId = UUID.randomUUID();

            return jdbc.queryForObject(sql,
                    (rs, rowNum) -> new AppSession(
                            UUID.fromString(rs.getString("id")),
                            UUID.fromString(rs.getString("user_id")),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getTimestamp("expires_at").toLocalDateTime()),
                    sessionId,
                    userId,
                    java.time.LocalDateTime.now().plusDays(30));

        } catch (DuplicateKeyException exception) {
            System.out.println(exception.getMessage());
            throw new RuntimeException("Session ID conflict, please try again.", exception);
        }
    }

    public Optional<AppSession> findSessionById(UUID sessionId) {
        try {
            String sql = """
                    SELECT id, user_id, created_at, expires_at
                    FROM app_sessions
                    WHERE id = ?
                    """;

            return jdbc.query(sql,
                    (rs, rowNum) -> new AppSession(
                            UUID.fromString(rs.getString("id")),
                            UUID.fromString(rs.getString("user_id")),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getTimestamp("expires_at").toLocalDateTime()),
                    sessionId).stream().findFirst();
        } catch (Exception e) {
            throw new RuntimeException("Error finding session by id", e);
        }
    }

    public void deleteSessionById(UUID sessionId) {
        try {
            String sql = "DELETE FROM app_sessions WHERE id = ?";
            jdbc.update(sql, sessionId);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
