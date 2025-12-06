package com.justtimeapi.api.services.proxy;
import com.justtimeapi.api.dto.request.*;
import com.justtimeapi.api.dto.response.ResourceClientResponse;
import com.justtimeapi.api.models.Resource;
import com.justtimeapi.api.interfaces.IResourceService;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class ResourceServiceProxy implements IResourceService{
    private final IResourceService target; // el servicio real

    @Override
    public Resource createResource(CreateResourceRequest request) {
        System.out.println("[AUDIT] Creating resource: " + request.name());
        Resource r = target.createResource(request);
        System.out.println("[AUDIT] Created ID: " + r.getId());
        return r;
    }

    @Override
    public Resource updateResourceById(UUID id, UpdateResourceRequest request) {
        System.out.println("[AUDIT] Updating resource: " + id);
        Resource updated = target.updateResourceById(id, request);
        System.out.println("[AUDIT] Resource updated.");
        return updated;
    }

    @Override
    public Optional<UUID> deleteResourceById(UUID id) {
        System.out.println("[AUDIT] Deleting resource: " + id);
        Optional<UUID> deleted = target.deleteResourceById(id);
        System.out.println("[AUDIT] Deleted: " + deleted);
        return deleted;
    }

    @Override
    public List<Resource> getAllResources() { return target.getAllResources(); }

    @Override
    public List<ResourceClientResponse> getResourcesForUser(UUID userId) { return target.getResourcesForUser(userId); }

    @Override
    public Optional<Resource> getResourceById(UUID id) { return target.getResourceById(id); }
}

