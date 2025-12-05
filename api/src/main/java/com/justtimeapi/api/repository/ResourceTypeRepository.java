package com.justtimeapi.api.repository;

import com.justtimeapi.api.models.ResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ResourceTypeRepository {
    private final JdbcTemplate jdbc;

    private final RowMapper<ResourceType> mapper = (ResultSet rs, int rowNum) -> {
        return ResourceType.builder()
                .id(rs.getInt("id"))
                .code(rs.getString("code"))
                .description(rs.getString("description"))
                .build();
    };

    public Optional<ResourceType> findResourceTypeByCode(String code){
        String sql = "SELECT * FROM resource_types WHERE code = ? LIMIT 1;";
        return jdbc.query(sql, mapper, code).stream().findFirst();
    }
}
