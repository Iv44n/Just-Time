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

        try {
            UUID id = jdbc.queryForObject(sql, UUID.class, roleName);
            return Optional.ofNullable(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
