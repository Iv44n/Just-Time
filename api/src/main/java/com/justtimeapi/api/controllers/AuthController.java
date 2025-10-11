package com.justtimeapi.api.controllers;

import com.justtimeapi.api.dto.request.LoginRequest;
import com.justtimeapi.api.dto.request.RegisterRequest;
import com.justtimeapi.api.dto.response.AuthResponse;
import com.justtimeapi.api.models.AppUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.justtimeapi.api.services.AuthService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.loginUser(request);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Refresh token required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        String refreshToken = authHeader.substring(7);
        String response = authService.refreshUserAccessToken(refreshToken);
        return ResponseEntity.ok(Map.of("accessToken", response));
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String refreshToken = authHeader.substring(7);
            authService.logout(refreshToken);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response.toString());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestParam String email) {
        Optional<AppUser> response = authService.getCurrentAppUser(email);
        return ResponseEntity.ok(response);
    }
}
