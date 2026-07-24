package com.ecoapi.techstore.cart.domain.exception;

import com.ecoapi.techstore.common.domain.exceptions.NotFoundException;

/**
 * Exception thrown when a product is not found or not available.
 * This is Cart context's own exception - it doesn't depend on Product context's exceptions.
 * This follows the Anti-Corruption Layer pattern.
 */
public class ProductNotFoundException extends NotFoundException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
