package com.justtimeapi.api.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class ApiError {
    private int status;
    private String message;
    private String errorCode;
    private Map<String, String> details;
}
