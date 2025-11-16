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
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyService {
    private final ApiKeyRepository apiKeyRepository;
    private final TemporaryKeyStorageStrategy keyStorageStrategy;

    public void saveApiKey(ApiKey apiKey){
        apiKeyRepository.save(apiKey);
    }

    public Optional<ApiKey> getByIdAndUserId(UUID keyId, UUID userId){
        return apiKeyRepository.findByIdAndUserId(keyId, userId);
    }

    public String revealKey(UUID keyId){
        return keyStorageStrategy.retrieve(keyId);
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

    public Instant generateExpiration(int durationHours) {
        return Instant.now().plus(durationHours, ChronoUnit.HOURS);
    }
}
