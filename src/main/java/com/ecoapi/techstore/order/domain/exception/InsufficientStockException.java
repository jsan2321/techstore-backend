package com.ecoapi.techstore.order.domain.exception;

/**
 * Exception thrown when there is insufficient stock to fulfill an order.
 * Extends the common InsufficientStockException so GlobalExceptionHandler can handle it.
 */
public class InsufficientStockException extends com.ecoapi.techstore.common.domain.exceptions.InsufficientStockException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
