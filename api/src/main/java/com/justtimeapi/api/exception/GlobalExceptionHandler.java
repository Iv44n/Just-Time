package com.justtimeapi.api.exception;

import com.justtimeapi.api.exception.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NoHandlerFoundException ex) {
        ApiError error = ApiError.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("The requested endpoint does not exist")
                .errorCode("ROUTE_NOT_FOUND")
                .details(Map.of("path", ex.getRequestURL()))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleDuplicateKeyException(UserAlreadyExistsException exception) {
        ApiError error = ApiError.builder()
                .status(HttpStatus.CONFLICT.value())
                .message(exception.getMessage())
                .errorCode(exception.getErrorCode())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        boolean isMissing = ex.getCause() == null || ex.getMessage().toLowerCase().contains("required request body is missing");

        ApiError error = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(isMissing
                        ? "Request body is missing."
                        : "The request body contains malformed JSON.")
                .errorCode(isMissing
                        ? "REQUEST_BODY_MISSING"
                        : "MALFORMED_JSON")
                .details(Map.of("error", ex.getMessage()))
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        ApiError error = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("One or more fields contain invalid values.")
                .errorCode("VALIDATION_FAILED")
                .details(fieldErrors)
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";

        ApiError error = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(String.format(
                        "Parameter '%s' with value '%s' is not of the expected type. Expected type: %s",
                        ex.getName(),
                        ex.getValue(),
                        expectedType
                ))
                .errorCode("METHOD_ARGUMENT_TYPE_MISMATCH")
                .details(Map.of(
                        "parameter", ex.getName(),
                        "value", String.valueOf(ex.getValue()),
                        "expectedType", expectedType
                ))
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParams(MissingServletRequestParameterException ex) {
        ApiError error = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.value())              // 400
                .message("Missing request parameter: " + ex.getParameterName())
                .errorCode("MISSING_REQUEST_PARAMETER")            // código interno
                .details(Map.of("parameter", ex.getParameterName()))
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException exception){
        ApiError error = ApiError.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(exception.getMessage())
                .errorCode(exception.getErrorCode())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ApiError> handleRoleNotFound(RoleNotFoundException exception) {
        ApiError error = ApiError.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(exception.getMessage())
                .errorCode(exception.getErrorCode())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiError> handleInvalidRefreshToken(InvalidRefreshTokenException exception){
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ApiError error = ApiError.builder()
                .status(status.value())
                .message(exception.getMessage())
                .errorCode(exception.getErrorCode())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ApiError> handleSessionNotFound(SessionNotFoundException ex) {
        ApiError error = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(ReadOnlyQueryException.class)
    public ResponseEntity<ApiError> handleReadOnlyQueryException(ReadOnlyQueryException ex) {

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                ex.getErrorCode(),
                Map.of("reason", "Only SELECT statements are allowed")
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}
