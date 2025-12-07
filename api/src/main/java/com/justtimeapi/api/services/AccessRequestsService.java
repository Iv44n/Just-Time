package com.justtimeapi.api.services;

import com.justtimeapi.api.dto.request.RequestAccessBody;
import com.justtimeapi.api.dto.response.AccessRequestResponse;
import com.justtimeapi.api.enums.AccessRequestStatus;
import com.justtimeapi.api.models.AccessRequest;
import com.justtimeapi.api.repository.AccessRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AccessRequestsService {
    private final AccessRequestRepository accessRequestRepository;

    public AccessRequest createRequest(RequestAccessBody accessRequest){
        return accessRequestRepository.save(accessRequest);
    }

    public List<AccessRequest> getAllRequests(){
        return accessRequestRepository.findAll();
    }

    public AccessRequestResponse getRequestsById(UUID requestId) {
        return accessRequestRepository.findByIdToAccessRequestResponse(requestId)
                .orElseThrow(() -> new RuntimeException("Access with " + requestId + " not found"));
    }

    public List<AccessRequest> getRequestsByUserId(UUID userId) {
        return accessRequestRepository.findByUserId(userId);
    }

    public Map<String, Object> approve(UUID requestId, UUID adminId){
        AccessRequest request = accessRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Access request not found"));

        if (request.getStatus() != AccessRequestStatus.PENDING) {
            throw new IllegalStateException("Request has already been reviewed");
        }

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
