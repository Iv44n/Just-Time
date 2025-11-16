package com.justtimeapi.api.repository;

import com.justtimeapi.api.models.ApiKey;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ApiKeyRepository {
    private final JdbcTemplate jdbc;

    private final RowMapper<ApiKey> mapper = (ResultSet rs, int rowNum) -> {
      return ApiKey.builder()
              .id(UUID.fromString(rs.getString("id")))
              .userId(UUID.fromString(rs.getString("user_id")))
              .resourceId(UUID.fromString(rs.getString("resource_id")))
              .keyPrefix(rs.getString("key_prefix"))
              .keyHash(rs.getString("key_hash"))
              .revoked(rs.getBoolean("revoked"))
              .revealed(rs.getBoolean("revealed"))
              .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
              .expiresAt(rs.getTimestamp("expires_at").toLocalDateTime())
              .build();
    };

    public void save(ApiKey apiKey){
        String sql = """
            INSERT INTO api_keys (user_id, resource_id, key_prefix, key_hash, expires_at)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (user_id, resource_id) DO NOTHING
            RETURNING *;
        """;

        jdbc.queryForObject(sql, mapper,
                apiKey.getUserId(),
                apiKey.getResourceId(),
                apiKey.getKeyPrefix(),
                apiKey.getKeyHash(),
                apiKey.getExpiresAt()
        );
    }

    public Optional<ApiKey> findByIdAndUserId(UUID keyId, UUID userId){
        String sql = "SELECT * FROM api_keys WHERE id = ? AND user_id = ?";
        return Optional.ofNullable(jdbc.queryForObject(sql, mapper, keyId, userId));
    }
}
