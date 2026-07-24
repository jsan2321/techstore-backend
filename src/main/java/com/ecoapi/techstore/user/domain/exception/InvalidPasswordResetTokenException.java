package com.ecoapi.techstore.user.domain.exception;

import com.ecoapi.techstore.common.domain.exceptions.DomainException;

/**
 * Exception thrown when a password reset token is invalid or expired.
 */
public class InvalidPasswordResetTokenException extends DomainException {

    public InvalidPasswordResetTokenException() {
        super("Invalid or expired password reset token");
    }
}
