package com.ecoapi.techstore.product.domain.model;

/**
 * Value Object representing a Category's unique identifier
 */
public record CategoryId(Long value) {
    
    public CategoryId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("CategoryId must be a positive number");
        }
    }
    
    public static CategoryId of(Long value) {
        return new CategoryId(value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
