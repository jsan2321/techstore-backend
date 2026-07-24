package com.ecoapi.techstore.product.domain.model;

/**
 * Value Object representing Brand identifier
 */
public record BrandId(Long value) {
    
    public BrandId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Brand ID must be a positive number");
        }
    }

    public static BrandId of(Long value) {
        return new BrandId(value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
