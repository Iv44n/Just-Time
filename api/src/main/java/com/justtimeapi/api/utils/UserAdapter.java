package com.justtimeapi.api.utils;

import com.justtimeapi.api.dto.response.UserResponse;
import com.justtimeapi.api.models.AppUser;

public class UserAdapter {
    public static UserResponse toResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
