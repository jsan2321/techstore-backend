package com.ecoapi.techstore.user.domain.exception;

import com.ecoapi.techstore.common.domain.exceptions.DomainException;

/**
 * Exception thrown when an email verification token is invalid or expired.
 */
public class InvalidEmailVerificationTokenException extends DomainException {

    public InvalidEmailVerificationTokenException() {
        super("Invalid or expired email verification token");
    }
}
