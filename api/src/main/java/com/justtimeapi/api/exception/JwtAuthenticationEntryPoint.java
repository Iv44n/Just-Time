package com.justtimeapi.api.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

        Map<String, Object> body = new HashMap<>();

        if (authException instanceof BadCredentialsException){
            body.put("errorCode", "InvalidCredentials");
            body.put("message", "Invalid Credentials");

            final ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), body);
            return;
        }

        String jwtError = (String) request.getAttribute("jwtError");

        if (jwtError != null) {
            body.put("errorCode", "InvalidAccessToken");
            body.put("message", jwtError);

            new ObjectMapper().writeValue(response.getOutputStream(), body);
            return;
        }

        body.put("errorCode", "Unauthorized");
        body.put("message", authException.getMessage());

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}
