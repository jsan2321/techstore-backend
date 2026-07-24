package com.ecoapi.techstore.cart.domain.exception;

import com.ecoapi.techstore.common.domain.exceptions.NotFoundException;

/**
 * Exception thrown when a cart is not found
 */
public class CartNotFoundException extends NotFoundException {
    public CartNotFoundException(String message) {
        super(message);
    }
}
