package com.justtimeapi.api.controllers;

import com.justtimeapi.api.dto.request.CreateResourceRequest;
import com.justtimeapi.api.dto.request.UpdateResourceRequest;
import com.justtimeapi.api.models.Resource;
import com.justtimeapi.api.services.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/admin/resources")
@RequiredArgsConstructor
public class AdminResourceController {
    private final ResourceService resourceService;

    @GetMapping()
    public ResponseEntity<?> resources(){
        return ResponseEntity.ok(resourceService.getAllResources());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return resourceService.getResourceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping()
    public ResponseEntity<?> createResource(@Valid @RequestBody CreateResourceRequest request){
        Resource resource = resourceService.createResource(request);

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.CREATED.value());
        response.put("message", "Resource created successfully");
        response.put("resource", resource);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteResource(@RequestParam UUID id) {
        Optional<UUID> deletedResourceId = resourceService.deleteResourceById(id);

        Map<String, Object> body = new HashMap<>();

        if (deletedResourceId.isPresent()) {
            body.put("status", HttpStatus.OK.value());
            body.put("message", "Resource deleted successfully.");
            body.put("deletedId", deletedResourceId.get());
            return ResponseEntity.ok(body);
        } else {
            body.put("status", HttpStatus.NOT_FOUND.value());
            body.put("error", "Resource not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
    }

    @PatchMapping()
    public ResponseEntity<?> updateResource(@RequestParam UUID id, @Valid @RequestBody UpdateResourceRequest request) {
        Resource updated = resourceService.updateResourceById(id, request);

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.OK.value());
        body.put("message", "Resource updated successfully");
        body.put("resource", updated);

        return ResponseEntity.ok(body);
    }
}
