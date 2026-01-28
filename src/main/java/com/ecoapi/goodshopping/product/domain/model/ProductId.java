package com.ecoapi.goodshopping.product.domain.model;

/**
 * Value Object representing a Product's unique identifier
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
