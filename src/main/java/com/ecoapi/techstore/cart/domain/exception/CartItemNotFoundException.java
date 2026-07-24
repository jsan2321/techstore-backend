package com.ecoapi.techstore.cart.domain.exception;

import com.ecoapi.techstore.common.domain.exceptions.NotFoundException;

/**
 * Exception thrown when a cart item is not found
 */
public class CartItemNotFoundException extends NotFoundException {
    public CartItemNotFoundException(String message) {
        super(message);
    }
}
