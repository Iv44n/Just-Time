package com.justtimeapi.api.adapters.resource;

import com.justtimeapi.api.models.ResourceDbDetails;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record ConnectionUrlAdapter(String connectionUrl) implements ResourceDetailsAdapter {
    @Override
    public ResourceDbDetails toResourceDbDetails(UUID resourceId) {
        ParsedDbUrl parsed = parseConnectionUrl(connectionUrl);

        return ResourceDbDetails.builder()
                .resourceId(resourceId)
                .engine(parsed.engine())
                .host(parsed.host())
                .port(parsed.port())
                .databaseName(parsed.database())
                .username(parsed.username())
                .encryptedPassword(encrypt(parsed.password()))
                .params(parsed.params())
                .build();
    }

    private ParsedDbUrl parseConnectionUrl(String url) {
        String DB_URL_PATTERN = "^(?<engine>[a-zA-Z0-9]+)://"
                + "(?:(?<username>[^:@]+)(?::(?<password>[^@]+))?@)?"
                + "(?<host>[^:/?]+)"
                + "(?::(?<port>\\d+))?"
                + "/(?<database>[^/?]+)"
                + "(?:\\?(?<params>.*))?$";

        Pattern regex = Pattern.compile(DB_URL_PATTERN);
        Matcher matcher = regex.matcher(url);

        if (!matcher.find()) {
            throw new RuntimeException("Invalid database connection URL format");
        }

        String engine = matcher.group("engine");
        String username = matcher.group("username");
        String password = matcher.group("password");
        String host = matcher.group("host");
        String portStr = matcher.group("port");
        String database = matcher.group("database");
        String paramsRaw = matcher.group("params");

        if (database == null || database.isBlank()) {
            throw new RuntimeException("Database name is required in connection URL");
        }

        Integer port = portStr != null ? Integer.valueOf(portStr) : null;
        return new ParsedDbUrl(engine, host, port, database, username, password, paramsRaw);
    }

    private record ParsedDbUrl(
            String engine,
            String host,
            Integer port,
            String database,
            String username,
            String password,
            String params
    ) {
    }

    private String encrypt(String password) {
        // TODO: Implementar cifrado real
        return password;
    }
}
