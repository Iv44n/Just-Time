package com.justtimeapi.api.exception.exceptions;

import lombok.Getter;

@Getter
public class InvalidRefreshTokenException extends RuntimeException {
    private final String errorCode = "INVALID_REFRESH_TOKEN";
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
