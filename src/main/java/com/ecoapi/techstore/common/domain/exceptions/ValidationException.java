package com.ecoapi.techstore.common.domain.exceptions;

/**
 * Exception thrown when domain validation fails
 */
public class ValidationException extends DomainException {
    
    public ValidationException(String message) {
        super(message);
    }
}
