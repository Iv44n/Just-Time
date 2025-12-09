package com.justtimeapi.api.exception.exceptions;

import lombok.Getter;

@Getter
public class ReadOnlyQueryException extends RuntimeException {
    private final String errorCode = "READ_ONLY_QUERY_NOT_ALLOWED";

    public ReadOnlyQueryException(String message) {
        super(message);
    }
}
