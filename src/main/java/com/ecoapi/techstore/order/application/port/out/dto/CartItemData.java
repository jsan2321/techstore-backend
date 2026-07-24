package com.ecoapi.techstore.order.application.port.out.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object for cart item information
 * Used for inter-context communication between Order and Cart contexts
 * This decouples Order from Cart's internal domain model
 */
public record CartItemData(
        Long productId,
        String productName,
    String productDescription,
    String productImageUrl,
        int quantity,
        BigDecimal unitPrice
) {
    public CartItemData {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or blank");
        }
        if (productDescription != null && productDescription.length() > 1000) {
            throw new IllegalArgumentException("Product description cannot exceed 1000 characters");
        }
        if (productImageUrl != null && productImageUrl.length() > 2048) {
            throw new IllegalArgumentException("Product image URL cannot exceed 2048 characters");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price cannot be null or negative");
        }
    }
    
    /**
     * Calculate total price for this item
     */
    public BigDecimal totalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
