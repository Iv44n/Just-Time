package com.justtimeapi.api.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CookieFactory {

    public ResponseCookie accessCookie(String token){
        return createCookie(Constants.ACCESS_TOKEN, token, Duration.ofMinutes(15));
    }

    public ResponseCookie refreshCookie(String token){
        return createCookie(Constants.REFRESH_TOKEN, token, Duration.ofDays(7));
    }

    private ResponseCookie createCookie(String name, String value, Duration maxAge){
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(maxAge)
                .build();
    }
}
