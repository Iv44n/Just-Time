package com.justtimeapi.api.exception;

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
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiError error;

        if (authException instanceof BadCredentialsException) {
            error = ApiError.builder()
                    .status(HttpServletResponse.SC_UNAUTHORIZED)
                    .errorCode("INVALID_CREDENTIALS")
                    .message("Invalid credentials")
                    .build();
        } else {
            String jwtError = (String) request.getAttribute("jwtError");

            if (jwtError != null) {
                error = ApiError.builder()
                        .status(HttpServletResponse.SC_UNAUTHORIZED)
                        .errorCode("INVALID_ACCESS_TOKEN")
                        .message(jwtError)
                        .build();
            } else {
                error = ApiError.builder()
                        .status(HttpServletResponse.SC_UNAUTHORIZED)
                        .errorCode("UNAUTHORIZED")
                        .message(authException.getMessage())
                        .build();
            }
        }

       mapper.writeValue(response.getOutputStream(), error);
    }
}
