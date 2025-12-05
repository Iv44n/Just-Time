package com.justtimeapi.api.controllers;

import com.justtimeapi.api.dto.request.LoginRequest;
import com.justtimeapi.api.dto.request.RegisterRequest;
import com.justtimeapi.api.dto.response.AuthResponse;
import com.justtimeapi.api.exception.ApiError;
import com.justtimeapi.api.exception.exceptions.UserNotFoundException;
import com.justtimeapi.api.models.AppUser;

import com.justtimeapi.api.utils.Constants;
import com.justtimeapi.api.utils.CookieFactory;
import com.justtimeapi.api.utils.UserAdapter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    AuthService getAuthService() {
        return authService;
    }

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
    public ResponseEntity<?> refreshToken(@CookieValue(name = Constants.REFRESH_TOKEN, required = false) String refreshToken) {
        if (refreshToken == null) {
            ApiError error = ApiError.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Refresh token not found")
                    .errorCode("REFRESH_TOKEN_MISSING")
                    .details(null)
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
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
    public ResponseEntity<?> logout(Authentication authentication) {
        authService.logout(authentication.getCredentials().toString());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        response.put("status", "success");

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(h -> {
                    cookieFactory.clearAuthCookies()
                            .forEach(cookie -> h.add(HttpHeaders.SET_COOKIE, cookie.toString()));
                })
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        AppUser response = authService.getCurrentAppUserByCookie(authentication.getCredentials().toString());
        return ResponseEntity.ok(UserAdapter.toResponse(response));
    }
}
