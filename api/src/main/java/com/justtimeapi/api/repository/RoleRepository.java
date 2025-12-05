package com.justtimeapi.api.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RoleRepository {
    private final JdbcTemplate jdbc;

    public Optional<UUID> findRoleIdByName(String roleName){
        String sql = "SELECT id FROM app_roles WHERE name = ?";
        return jdbc.query(
                sql,
                (rs ,rowNum) -> UUID.fromString(rs.getString("id")),
                roleName
        ).stream().findFirst();
    }
}
