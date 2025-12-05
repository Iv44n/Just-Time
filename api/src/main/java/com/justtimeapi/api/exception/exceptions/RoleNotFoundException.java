package com.justtimeapi.api.exception.exceptions;

import lombok.Getter;

@Getter
public class RoleNotFoundException extends RuntimeException {
    private final String errorCode = "ROLE_NOT_FOUND";
    public RoleNotFoundException(String message) {
        super(message);
    }
}
