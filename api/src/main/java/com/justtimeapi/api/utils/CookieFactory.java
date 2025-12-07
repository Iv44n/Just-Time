package com.justtimeapi.api.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CookieFactory {

    @Value("${APP_ENV:dev}")
    private String appEnv;

    private boolean isProd(){
        return appEnv.equalsIgnoreCase("prod");
    }

    public ResponseCookie accessCookie(String token){
        return createCookie(Constants.ACCESS_TOKEN, token, "/", Duration.ofMinutes(15));
    }

    public ResponseCookie refreshCookie(String token){
        return createCookie(Constants.REFRESH_TOKEN, token, "/api/auth/refresh", Duration.ofDays(7));
    }

    public List<ResponseCookie> clearAuthCookies() {
        ResponseCookie access = clearCookie(Constants.ACCESS_TOKEN, "/");
        ResponseCookie refresh = clearCookie(Constants.REFRESH_TOKEN, "/api/auth/refresh");

        return List.of(access, refresh);
    }

    private ResponseCookie clearCookie(String name, String path){
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(isProd())
                .path(path)
                .maxAge(0)
                .sameSite(isProd() ? "None" : "Lax")
                .build();
    }

    private ResponseCookie createCookie(String name, String value, String path,Duration maxAge){
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(isProd())
                .path(path)
                .sameSite(isProd() ? "None" : "Lax")
                .maxAge(maxAge)
                .build();
    }
}
