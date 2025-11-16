package com.justtimeapi.api.services;

import com.justtimeapi.api.dto.request.RequestAccessBody;
import com.justtimeapi.api.enums.AccessRequestStatus;
import com.justtimeapi.api.models.AccessRequest;
import com.justtimeapi.api.models.ApiKey;
import com.justtimeapi.api.repository.AccessRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AccessRequestsService {
    private final AccessRequestRepository accessRequestRepository;
    private final ApiKeyService apiKeyService;

    public AccessRequest createRequest(RequestAccessBody accessRequest){
        return accessRequestRepository.save(accessRequest);
    }

    public List<AccessRequest> getAllRequests(){
        return accessRequestRepository.findAll();
    }

    public void updateStatus(UUID id, AccessRequestStatus status, UUID adminId) {
        accessRequestRepository.updateStatus(id, status, adminId);
    }

    public Map<String, Object> approve(UUID requestId, UUID adminId){
        AccessRequest request = accessRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Access request not found"));

        if (request.getStatus() != AccessRequestStatus.PENDING) {
            throw new IllegalStateException("Request has already been reviewed");
        }

        String keyPrefix = "jt";
        String rawKey = apiKeyService.generateTempKey(keyPrefix);
        String keyHash = apiKeyService.hashKey(rawKey);
        Instant expiration = apiKeyService.generateExpiration(request.getRequestedHours());

        apiKeyService.saveApiKey(ApiKey.builder()
                .userId(request.getUserId())
                .resourceId(request.getResourceId())
                .keyHash(keyHash)
                .keyPrefix(keyPrefix)
                .expiresAt(LocalDateTime.from(expiration))
                .build()
        );

        accessRequestRepository.updateStatus(requestId, AccessRequestStatus.APPROVED, adminId);

        Map<String, Object> res = new HashMap<>();
        res.put("accessRequestId", request.getId());
        res.put("requestStatus", AccessRequestStatus.APPROVED);
        return res;
    }

    public String revealKey(UUID keyId, UUID userId){
        ApiKey apiKey = apiKeyService.getByIdAndUserId(keyId, userId)
                .orElseThrow(() -> new RuntimeException("API key not found"));

        if (apiKey.isRevealed()) {
            throw new IllegalStateException("API key has already been revealed");
        }

        String rawKey = apiKeyService.revealKey(apiKey.getId());
        apiKey.setRevealed(true);

        return rawKey;
    }

    public void reject(UUID id, UUID adminId){
        accessRequestRepository.updateStatus(id, AccessRequestStatus.REJECTED, adminId);
    }
}
