package com.justtimeapi.api.exception.exceptions;

import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends RuntimeException {
    private final String errorCode = "USER_ALREADY_EXISTS";
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
