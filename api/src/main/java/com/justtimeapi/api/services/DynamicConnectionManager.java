package com.justtimeapi.api.services;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DynamicConnectionManager {
    private final Map<UUID, HikariDataSource> pools = new ConcurrentHashMap<>();

    public DataSource getDataSource(String url, String username, String password, String driverClass, UUID resourceId) {
        return pools.computeIfAbsent(resourceId, id -> createPool(url, username, password, driverClass, resourceId));
    }

    private HikariDataSource createPool(String url, String username, String password, String driverClass, UUID resourceId) {
        HikariConfig hikari = new HikariConfig();

        hikari.setJdbcUrl(url);
        hikari.setUsername(username);
        hikari.setPassword(password);
        hikari.setDriverClassName(driverClass);
        hikari.setMaximumPoolSize(5);
        hikari.setMinimumIdle(1);
        hikari.setPoolName("dynamic-pool-" + resourceId);

        return new HikariDataSource(hikari);
    }
}
