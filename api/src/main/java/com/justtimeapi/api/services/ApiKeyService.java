package com.justtimeapi.api.services;

import com.justtimeapi.api.interfaces.TemporaryKeyStorageStrategy;
import com.justtimeapi.api.models.ApiKey;
import com.justtimeapi.api.repository.ApiKeyRepository;
import com.justtimeapi.api.services.apiKeyManager.InMemoryKeyStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyService {
    private final ApiKeyRepository apiKeyRepository;
    private final TemporaryKeyStorageStrategy keyStorageStrategy;

    public ApiKey saveApiKey(ApiKey apiKey){
        return apiKeyRepository.save(apiKey);
    }
    public void saveApiKeyInStorageKey(UUID apiKeyId, String rawKey){keyStorageStrategy.save(apiKeyId, rawKey);}

    public Map<String, Object> revealKey(UUID resourceId, UUID userId){
        ApiKey apiKey = apiKeyRepository.findByResourceIdAndUserId(resourceId, userId)
                .orElseThrow(() -> new RuntimeException("API key not found"));

        if (apiKey.isRevealed()) {
            throw new IllegalStateException("API key has already been revealed");
        }

        String rawKey = keyStorageStrategy.retrieve(apiKey.getId());
        apiKey.setRevealed(true);

        return Map.of(
                "apiKeyId", apiKey.getId(),
                "userId",apiKey.getUserId(),
                "apiKey", rawKey
        );
    }

    public String generateTempKey(String keyPrefix) {
        String rawUuid = UUID.randomUUID().toString().replace("-", "");
        return keyPrefix + "_" + rawUuid;
    }

    public String hashKey(String rawKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawKey.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing key", e);
        }
    }

    public LocalDateTime generateExpiration(int durationHours) {
        return LocalDateTime.now().plusHours(durationHours);
    }
}
