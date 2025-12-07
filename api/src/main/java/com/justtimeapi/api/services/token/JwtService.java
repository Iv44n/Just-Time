package com.justtimeapi.api.services.token;

import com.justtimeapi.api.enums.Token;
import com.justtimeapi.api.interfaces.TokenGeneratorStrategy;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;
    private final Map<Token, TokenGeneratorStrategy> tokenGeneratorsStrategies;

    public JwtService(
            AccessTokenGenerator accessTokenGenerator,
            RefreshTokenGenerator refreshTokenGenerator) {
        this.tokenGeneratorsStrategies = Map.of(
                Token.ACCESS_TOKEN, accessTokenGenerator,
                Token.REFRESH_TOKEN, refreshTokenGenerator);
    }

    public String generateToken(Token type, UUID sessionId, UUID userId) {
        TokenGeneratorStrategy strategy = tokenGeneratorsStrategies.get(type);

        if (strategy == null) {
            throw new IllegalArgumentException("No strategy registered for token type " + type);
        }

        return strategy.generateToken(sessionId, userId, jwtSecret);
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getSubject() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getSubject() == null && claims.get("sessionId") != null;
        } catch (Exception e) {
            return false;
        }
    }

    public UUID extractUserId(String token) {
        Claims claims = extractClaims(token);
        String subject = claims.getSubject();
        return subject != null ? UUID.fromString(subject) : null;
    }

    public UUID extractSessionId(String token) {
        String sessionId = extractClaims(token).get("sessionId", String.class);
        return sessionId != null ? UUID.fromString(sessionId) : null;
    }
}
