package com.justtimeapi.api.controllers;

import com.justtimeapi.api.dto.request.LoginRequest;
import com.justtimeapi.api.dto.request.RegisterRequest;
import com.justtimeapi.api.dto.response.AuthResponse;
import com.justtimeapi.api.models.AppUser;

import com.justtimeapi.api.utils.Constants;
import com.justtimeapi.api.utils.CookieFactory;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.justtimeapi.api.services.AuthService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CookieFactory cookieFactory;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.createAccount(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(h -> {
                    h.add(HttpHeaders.SET_COOKIE, cookieFactory.accessCookie(response.accessToken()).toString());
                    h.add(HttpHeaders.SET_COOKIE, cookieFactory.refreshCookie(response.refreshToken()).toString());
                })
                .body(response.user());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.loginUser(request);

        return ResponseEntity
                .ok()
                .headers(h -> {
                    h.add(HttpHeaders.SET_COOKIE, cookieFactory.accessCookie(response.accessToken()).toString());
                    h.add(HttpHeaders.SET_COOKIE, cookieFactory.refreshCookie(response.refreshToken()).toString());
                })
                .body(response.user());
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token not found");
        }

        String refreshToken = Arrays.stream(cookies)
                .filter(cookie -> Constants.REFRESH_TOKEN.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token not found");
        }

        String newAccessToken = authService.refreshUserAccessToken(refreshToken);

        return ResponseEntity
                .ok()
                .headers(h -> {
                    h.add(HttpHeaders.SET_COOKIE, cookieFactory.accessCookie(newAccessToken).toString());
                })
                .body("Access token refreshed successfully");
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
