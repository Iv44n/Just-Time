package com.justtimeapi.api.exception.exceptions;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final String errorCode = "USER_NOT_FOUND";
    public UserNotFoundException(String message) {
        super(message);
    }
}
