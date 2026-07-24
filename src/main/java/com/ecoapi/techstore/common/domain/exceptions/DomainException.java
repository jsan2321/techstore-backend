package com.ecoapi.techstore.common.domain.exceptions;

/**
 * Base exception for all domain-level exceptions
 */
public class DomainException extends RuntimeException {
    
    public DomainException(String message) {
        super(message);
    }
    
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
