package com.ecoapi.techstore.user.domain.exception;

import com.ecoapi.techstore.common.domain.exceptions.DomainException;

/**
 * Exception thrown when current password does not match persisted credentials.
 */
public class InvalidCurrentPasswordException extends DomainException {

    public InvalidCurrentPasswordException() {
        super("Current password is invalid");
    }
}
