package com.justtimeapi.api.services;

import com.justtimeapi.api.dto.request.RequestAccessBody;
import com.justtimeapi.api.enums.AccessRequestStatus;
import com.justtimeapi.api.models.AccessRequest;
import com.justtimeapi.api.models.ApiKey;
import com.justtimeapi.api.repository.AccessRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public List<AccessRequest> getRequestsByUserId(UUID userId) {
        return accessRequestRepository.findByUserId(userId);
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
        LocalDateTime expiration = apiKeyService.generateExpiration(request.getRequestedHours());

        ApiKey apiKey = apiKeyService.saveApiKey(ApiKey.builder()
                .userId(request.getUserId())
                .resourceId(request.getResourceId())
                .keyHash(keyHash)
                .keyPrefix(keyPrefix)
                .expiresAt(expiration)
                .build()
        );

        apiKeyService.saveApiKeyInStorageKey(apiKey.getId(), rawKey);
        accessRequestRepository.updateStatus(requestId, AccessRequestStatus.APPROVED, adminId);

        Map<String, Object> res = new HashMap<>();
        res.put("accessRequestId", request.getId());
        res.put("requestStatus", AccessRequestStatus.APPROVED);
        return res;
    }

    public void reject(UUID id, UUID adminId){
        accessRequestRepository.updateStatus(id, AccessRequestStatus.REJECTED, adminId);
    }
}
