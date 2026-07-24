package com.ecoapi.techstore.common.domain.valueobjects;

/**
 * Value Object representing a Product's unique identifier
 * Part of the Shared Kernel - used across multiple bounded contexts
 */
public record ProductId(Long value) {
    
    public ProductId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("ProductId must be a positive number");
        }
    }
    
    public static ProductId of(Long value) {
        return new ProductId(value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
