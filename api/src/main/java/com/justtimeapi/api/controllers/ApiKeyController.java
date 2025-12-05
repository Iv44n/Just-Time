package com.justtimeapi.api.controllers;

import com.justtimeapi.api.services.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {
    private final ApiKeyService apiKeyService;

    @GetMapping("reveal")
    public ResponseEntity<Map<String, Object>> revealApiKey(
            @RequestParam UUID resourceId,
            @RequestParam UUID userId) {
        Map<String, Object> apiKeyRevealedData = apiKeyService.revealKey(resourceId, userId);

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.OK.value());
        body.put("message", "API key successfully revealed");
        body.put("data", apiKeyRevealedData);

        return ResponseEntity.ok(body);
    }
}
