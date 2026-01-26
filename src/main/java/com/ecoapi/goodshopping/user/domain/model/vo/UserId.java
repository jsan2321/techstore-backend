package com.ecoapi.goodshopping.user.domain.model.vo;

/**
 * Value Object representing a User's unique identifier
 * Provides type safety and prevents mixing IDs from different aggregates
 */
public record UserId(Long value) {
    
    public UserId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("UserId must be a positive number");
        }
    }
    
    public static UserId of(Long value) {
        return new UserId(value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
