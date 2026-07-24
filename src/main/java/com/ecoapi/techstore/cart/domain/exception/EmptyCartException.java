package com.ecoapi.techstore.cart.domain.exception;

import com.ecoapi.techstore.common.domain.exceptions.DomainException;

/**
 * Exception thrown when an operation requires a non-empty cart
 */
public class EmptyCartException extends DomainException {
    public EmptyCartException(String message) {
        super(message);
    }
}
