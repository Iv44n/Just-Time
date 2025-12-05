package com.justtimeapi.api.controllers;

import com.justtimeapi.api.dto.request.CreateResourceRequest;
import com.justtimeapi.api.dto.request.UpdateResourceRequest;
import com.justtimeapi.api.models.Resource;
import com.justtimeapi.api.models.UserPrincipal;
import com.justtimeapi.api.services.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/resources")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @GetMapping()
    public ResponseEntity<?> resources(
            @RequestParam(required = false) String status,
            Authentication authentication
    ){
        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        var userPrincipal = ((UserPrincipal) authentication.getPrincipal());

        if (!isAdmin) {

            return ResponseEntity.ok(resourceService.getResourcesForUser(userPrincipal.getId()));
        }

        if (status != null && !status.isBlank()) {
            System.out.println("Falta función por status: " + status);
        }

        return ResponseEntity.ok(resourceService.getAllResources());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return resourceService.getResourceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<?> createResource(@Valid @RequestBody CreateResourceRequest request){
        Resource resource = resourceService.createResource(request);

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.CREATED.value());
        response.put("message", "Resource created successfully");
        response.put("resource", resource);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize("hasRole('ADMIN')")
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
