package com.ecoapi.techstore.user.domain.exception;

import com.ecoapi.techstore.common.domain.exceptions.DomainException;

/**
 * Exception thrown when login credentials are invalid
 */
public class InvalidCredentialsException extends DomainException {
    
    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
