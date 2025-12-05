package com.justtimeapi.api.exception.exceptions;

import lombok.Getter;

@Getter
public class SessionNotFoundException extends RuntimeException {
    private final String errorCode = "INVALID_SESSION";
    public SessionNotFoundException(String message) {
        super(message);
    }
}
