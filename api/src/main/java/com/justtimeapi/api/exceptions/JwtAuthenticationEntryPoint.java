package com.justtimeapi.api.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        int status = authException.getMessage().contains("Invalid credentials")
                ? HttpServletResponse.SC_BAD_REQUEST
                : HttpServletResponse.SC_UNAUTHORIZED;

        String errorType = authException.getMessage().contains("Invalid credentials") ? "Bad request" : "Unauthorized";

        final Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("error", errorType);
        body.put("message", authException.getMessage());
        // body.put("errors", authException);

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
