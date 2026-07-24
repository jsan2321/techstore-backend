package com.ecoapi.techstore.cart.domain.model;

/**
 * Value Object representing CartItem identifier
 * Uses record for immutability and consistent API with other value objects
 */
public record CartItemId(Long value) {
    
    public CartItemId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("CartItemId must be a positive number");
        }
    }
    
    public static CartItemId of(Long value) {
        return new CartItemId(value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
