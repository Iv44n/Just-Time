package com.justtimeapi.api.services;

import com.justtimeapi.api.dto.request.CreateResourceRequest;
import com.justtimeapi.api.dto.request.UpdateResourceRequest;
import com.justtimeapi.api.models.Resource;
import com.justtimeapi.api.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public List<Resource> getAllResources(){
        return resourceRepository.findAll();
    }

    public Optional<Resource> getResourceById(UUID id){
        return resourceRepository.findById(id);
    }

    public Resource createResource(CreateResourceRequest resourceRequest){
        Resource resource = Resource.builder()
                .name(resourceRequest.name())
                .type(resourceRequest.type())
                .connectionUrl(resourceRequest.connectionUrl())
                .username(resourceRequest.username())
                .password(resourceRequest.password())
                .build();

        return resourceRepository.save(resource);
    }

    public Optional<UUID> deleteResourceById(UUID id){
        return resourceRepository.delete(id);
    }

    public Resource updateResourceById(UUID resourceId, UpdateResourceRequest resourceRequest){
        Optional<Resource> existsResource = resourceRepository.findById(resourceId);

        if(existsResource.isEmpty()){
            throw new RuntimeException("Resource not found");
        }

        return resourceRepository.updateResourceById(resourceId, resourceRequest);
    }
}
