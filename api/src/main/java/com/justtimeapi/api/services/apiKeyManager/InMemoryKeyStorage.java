package com.justtimeapi.api.services.apiKeyManager;

import com.justtimeapi.api.interfaces.TemporaryKeyStorageStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryKeyStorage implements TemporaryKeyStorageStrategy {
    private final Map<UUID, String> tempKeys = new ConcurrentHashMap<>();

    @Override
    public void save(UUID keyId, String rawKey) {
        tempKeys.put(keyId, rawKey);
    }

    @Override
    public String retrieve(UUID keyId) {
        String key = tempKeys.remove(keyId);

        if (key == null) {
            throw new RuntimeException("Key already revealed or not found");
        }

        return key;
    }
}
