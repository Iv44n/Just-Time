package com.justtimeapi.api.dto.response;

public record AuthResponse(String accessToken, String refreshToken, UserResponse user) {}

