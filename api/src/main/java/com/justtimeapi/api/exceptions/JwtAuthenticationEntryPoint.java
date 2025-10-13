package com.justtimeapi.api.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
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
            AuthenticationException authException
    ) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        if (authException instanceof BadCredentialsException){
            final Map<String, Object> body = new HashMap<>();
            body.put("errorCode", "InvalidCredentials");
            body.put("message", "Invalid Credentials");

            final ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), body);
        }

        final Map<String, Object> body = new HashMap<>();
        body.put("errorCode", "InvalidAccessToken");
        body.put("message", resolveMessage(authException));

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }

    private String resolveMessage(AuthenticationException ex) {
        String msg = ex.getMessage().toLowerCase();
        if (msg.contains("expired")) return "Access token has expired";
        if (msg.contains("missing")) return "Access token is missing";
        if (msg.contains("invalid")) return "Access token is invalid";
        return "Unauthorized access";
    }
}
