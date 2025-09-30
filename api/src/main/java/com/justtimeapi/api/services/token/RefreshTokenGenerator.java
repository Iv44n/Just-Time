package com.justtimeapi.api.services.token;

import com.justtimeapi.api.interfaces.TokenGeneratorI;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class RefreshTokenGenerator implements TokenGeneratorI {
    @Value("${jwt.refresh.expiration}")
    private Long refreshExpiration;

    @Override
    public String generateToken(UUID sessionId, UUID userId, String jwtSecret) {
        return Jwts.builder()
                .claim("sessionId", sessionId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();
    }
}
