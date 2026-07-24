package com.ecoapi.techstore.cart.domain.model;

/**
 * Value Object representing Cart identifier
 * Uses record for immutability and consistent API with other value objects
 */
public record CartId(Long value) {
    
    public CartId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("CartId must be a positive number");
        }
    }
    
    public static CartId of(Long value) {
        return new CartId(value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
