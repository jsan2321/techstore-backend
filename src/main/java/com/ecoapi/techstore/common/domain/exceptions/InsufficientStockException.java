package com.ecoapi.techstore.common.domain.exceptions;

/**
 * Base exception for insufficient stock conditions.
 * Each bounded context can extend this or use it directly.
 * This allows the GlobalExceptionHandler to catch all stock-related exceptions.
 */
public class InsufficientStockException extends DomainException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
