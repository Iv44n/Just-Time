package com.justtimeapi.api.interfaces;
import com.justtimeapi.api.dto.request.*;
import com.justtimeapi.api.dto.response.ResourceClientResponse;
import com.justtimeapi.api.models.Resource;
import java.util.*;

public interface IResourceService {
    List<Resource> getAllResources();
    List<ResourceClientResponse> getResourcesForUser(UUID userId);
    Optional<Resource> getResourceById(UUID id);
    Resource createResource(CreateResourceRequest resourceRequest);
    Optional<UUID> deleteResourceById(UUID id);
    Resource updateResourceById(UUID resourceId, UpdateResourceRequest resourceRequest);
}
