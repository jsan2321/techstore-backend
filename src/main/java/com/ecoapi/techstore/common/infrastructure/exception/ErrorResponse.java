package com.ecoapi.techstore.common.infrastructure.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error response for all API exceptions
 */
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path,
    List<ValidationError> validationErrors
) {
    
    public ErrorResponse(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path, null);
    }
    
    public ErrorResponse(int status, String error, String message, String path, List<ValidationError> validationErrors) {
        this(LocalDateTime.now(), status, error, message, path, validationErrors);
    }
    
    /**
     * Validation error detail
     */
    public record ValidationError(
        String field,
        String rejectedValue,
        String message
    ) {}
}
