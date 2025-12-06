package com.justtimeapi.api.services;

import com.justtimeapi.api.adapters.resource.ResourceDetailsAdapter;
import com.justtimeapi.api.adapters.resource.ResourceDetailsAdapterFactory;
import com.justtimeapi.api.dto.request.*;
import com.justtimeapi.api.dto.response.ResourceClientResponse;
import com.justtimeapi.api.enums.Roles;
import com.justtimeapi.api.interfaces.IResourceService;
import com.justtimeapi.api.models.Resource;
import com.justtimeapi.api.models.ResourceDbDetails;
import com.justtimeapi.api.models.ResourceType;
import com.justtimeapi.api.repository.ResourceDbDetailsRepository;
import com.justtimeapi.api.repository.ResourceRepository;
import com.justtimeapi.api.repository.ResourceTypeRepository;

import com.justtimeapi.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ResourceService implements IResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceTypeRepository resourceTypeRepository;
    private final ResourceDbDetailsRepository resourceDbDetailsRepository;
    private final UserRepository userRepository;

    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    public List<ResourceClientResponse> getResourcesForUser(UUID userId){
        return resourceRepository.findResourcesForUser(userId);
    }

    public Optional<Resource> getResourceById(UUID id) {
        return resourceRepository.findById(id);
    }

    @Transactional
    public Resource createResource(CreateResourceRequest resourceRequest) {
        boolean isAdmin = userRepository
                .findUserById(resourceRequest.createdBy())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getRoles().contains(Roles.ROLE_ADMIN);

        if (!isAdmin) {
            throw new RuntimeException("User is not an admin");
        }

        ResourceType resourceType = resourceTypeRepository.findResourceTypeByCode(resourceRequest.typeCode())
                .orElseThrow(() -> new RuntimeException("Resource type not found"));

        Resource resourceToSave = Resource.builder()
                .name(resourceRequest.name())
                .type(resourceType)
                .createdBy(resourceRequest.createdBy())
                .build();
        Resource resource = resourceRepository.save(resourceToSave);

        ResourceDetailsAdapter adapter = ResourceDetailsAdapterFactory.create(resourceRequest.details());
        ResourceDbDetails dbDetails = adapter.toResourceDbDetails(resource.getId());
        resourceDbDetailsRepository.save(dbDetails);

        return resource;
    }

    @Transactional
    public Optional<UUID> deleteResourceById(UUID id) {
        return resourceRepository.delete(id);
    }

    @Transactional
    public Resource updateResourceById(UUID resourceId, UpdateResourceRequest resourceRequest) {
        Optional<Resource> existsResource = resourceRepository.findById(resourceId);

        if (existsResource.isEmpty()) {
            throw new RuntimeException("Resource not found");
        }

        return resourceRepository.updateResourceById(resourceId, resourceRequest);
    }
}
