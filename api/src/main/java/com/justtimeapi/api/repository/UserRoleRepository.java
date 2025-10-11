package com.justtimeapi.api.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRoleRepository {
    private final JdbcTemplate jdbc;

    public void assignRoleToUser(UUID userId, UUID roleId){
        String sql = "INSERT INTO app_user_roles (user_id, role_id) VALUES (?, ?)";
        try {
            jdbc.update(sql, userId, roleId);
        } catch (Exception e) {
            throw new RuntimeException("Error assigning role to user");
        }
    }

    public List<String> findRolesByUserId(UUID userId) {
        String sql = """
        SELECT r.name
        FROM app_user_roles ur
        JOIN app_roles r ON ur.role_id = r.id
        WHERE ur.user_id = ?
        """;

        try {
            return jdbc.query(sql, (rs, rowNum) -> rs.getString("name"), userId);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
