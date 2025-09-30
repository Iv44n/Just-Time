package com.justtimeapi.api.services.token;

import com.justtimeapi.api.interfaces.TokenGeneratorI;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class AccessTokenGenerator implements TokenGeneratorI {
    @Value("${jwt.access.expiration}")
    private Long accessExpiration;

    @Override
    public String generateToken(UUID sessionId, UUID userId, String jwtSecret) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("sessionId", sessionId.toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();
    }
}
