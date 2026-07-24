package com.ecoapi.techstore.cart.application.port.out.dto;

import com.ecoapi.techstore.common.domain.valueobjects.Money;

/**
 * Data Transfer Object for product information needed by Cart context
 * Used for inter-context communication between Cart and Product contexts
 * This decouples Cart from Product's internal domain model
 */
public record ProductData(
        Long productId,
        String name,
    String description,
    String imageUrl,
    Money originalPrice,
    Money effectivePrice,
    Integer discountPercentage,
        int availableStock,
        boolean active
) {
    public ProductData {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or blank");
        }
        if (originalPrice == null) {
            throw new IllegalArgumentException("Original price cannot be null");
        }
        if (effectivePrice == null) {
            throw new IllegalArgumentException("Effective price cannot be null");
        }
    }
    
    /**
     * Check if the product has sufficient stock
     */
    public boolean hasStock(int quantity) {
        return availableStock >= quantity;
    }
}
