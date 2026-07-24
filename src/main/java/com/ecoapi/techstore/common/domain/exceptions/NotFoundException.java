package com.ecoapi.techstore.common.domain.exceptions;

/**
 * Exception thrown when a requested entity is not found
 */
public class NotFoundException extends DomainException {
    
    public NotFoundException(String message) {
        super(message);
    }
}
