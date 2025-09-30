package com.justtimeapi.api.repository;

import com.justtimeapi.api.exceptions.UserAlreadyExistsException;
import com.justtimeapi.api.models.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbc;

    public AppUser createUser(AppUser user) {
        try {
            String sql = """
                    INSERT INTO app_users (id, username, email, password)
                    VALUES (?, ?, ?, ?)
                    RETURNING id, username, email, password, created_at, updated_at
                    """;

            UUID userId = UUID.randomUUID();

            return jdbc.queryForObject(sql,
                    (rs, rowNum) -> new AppUser(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getTimestamp("updated_at").toLocalDateTime()),
                    userId,
                    user.getUsername(),
                    user.getEmail(),
                    user.getPassword());
        } catch (DuplicateKeyException e) {
            Throwable root = e.getMostSpecificCause();

            String msg = "Duplicate data";
            boolean usernameOrEmailExists = root.getMessage().contains("app_users_username_key")
                    || root.getMessage().contains("app_users_email_key");

            if (usernameOrEmailExists) {
                msg = "The username or email is already in use";
            }

            throw new UserAlreadyExistsException(msg, e);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Invalid data");
        } catch (DataAccessException e) {
            throw new RuntimeException("Database access error");
        }
    }

    public Optional<AppUser> findUserByEmail(String email) {
        try {
            String sql = """
                    SELECT id, username, email, password, created_at, updated_at
                    FROM app_users
                    WHERE email = ?
                    """;

            return jdbc.query(sql,
                    (rs, rowNum) -> new AppUser(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getTimestamp("updated_at").toLocalDateTime()),
                    email).stream().findFirst();
        } catch (Exception e) {
            throw new RuntimeException("Error finding user by email", e);
        }
    }

    public Optional<AppUser> findUserById(UUID userId) {
        try {
            String sql = """
                    SELECT id, username, email, password, created_at, updated_at
                    FROM app_users
                    WHERE id = ?
                    """;

            return jdbc.query(sql,
                    (rs, rowNum) -> new AppUser(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getTimestamp("updated_at").toLocalDateTime()),
                    userId).stream().findFirst();
        } catch (Exception e) {
            throw new RuntimeException("Error finding user by id", e);
        }
    }
}
