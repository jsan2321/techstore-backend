package com.ecoapi.techstore.product.domain.exception;

/**
 * Exception thrown when there is insufficient stock for a product operation.
 * Extends the common InsufficientStockException so GlobalExceptionHandler can handle it.
 */
public class InsufficientStockException extends com.ecoapi.techstore.common.domain.exceptions.InsufficientStockException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
