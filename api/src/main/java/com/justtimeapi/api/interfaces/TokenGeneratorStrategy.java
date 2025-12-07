package com.justtimeapi.api.interfaces;

import java.util.UUID;

public interface TokenGeneratorStrategy {
    String generateToken(UUID sessionId, UUID userId, String jwtSecret);
}
