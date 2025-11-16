package com.justtimeapi.api.interfaces;

import java.util.UUID;

public interface TemporaryKeyStorageStrategy {
    void save(UUID keyId, String rawKey);
    String retrieve(UUID keyId);
}
