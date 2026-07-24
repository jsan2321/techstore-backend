package com.ecoapi.techstore.user.domain.exception;

import com.ecoapi.techstore.common.domain.exceptions.DomainException;

/**
 * Exception thrown when a user attempts to authenticate without verifying email.
 */
public class EmailNotVerifiedException extends DomainException {

    public EmailNotVerifiedException(String email) {
        super("Email is not verified for account: " + email);
    }
}
