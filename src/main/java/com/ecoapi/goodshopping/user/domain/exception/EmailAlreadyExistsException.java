package com.ecoapi.goodshopping.user.domain.exception;

import com.ecoapi.goodshopping.common.domain.exceptions.DomainException;

/**
 * Exception thrown when attempting to register a user with an email that already exists
 */
public class EmailAlreadyExistsException extends DomainException {
    
    public EmailAlreadyExistsException(String email) {
        super("Email already exists: " + email);
    }
}
