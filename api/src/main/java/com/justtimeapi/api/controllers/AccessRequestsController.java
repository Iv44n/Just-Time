package com.justtimeapi.api.controllers;

import com.justtimeapi.api.dto.request.RequestAccessBody;
import com.justtimeapi.api.enums.AccessRequestStatus;
import com.justtimeapi.api.models.AccessRequest;
import com.justtimeapi.api.services.AccessRequestsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/access-requests")
@RequiredArgsConstructor
public class AccessRequestsController {
    private final AccessRequestsService accessRequestsService;

    @PostMapping
    public ResponseEntity<?> requestAccess(@Valid @RequestBody RequestAccessBody request){
        AccessRequest accessRequest = accessRequestsService.createRequest(request);

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.CREATED.value());
        body.put("message", "Access request created successfully");
        body.put("access_request", accessRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("all")
    public ResponseEntity<?> getAllRequests(){ return ResponseEntity.ok(accessRequestsService.getAllRequests()); }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}/approve")
    public ResponseEntity<?> approve(@PathVariable UUID id, @RequestParam UUID adminId) {
        System.out.println(id.toString() + adminId.toString());
        Map<String, Object> body = accessRequestsService.approve(id, adminId);
        body.put("status", HttpStatus.OK.value());
        body.put("message", "Request approved");

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}/reject")
    public ResponseEntity<?> reject(@PathVariable UUID id, @RequestParam UUID adminId) {
        accessRequestsService.reject(id, adminId);

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.OK.value());
        body.put("message", "Request rejected");

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}
