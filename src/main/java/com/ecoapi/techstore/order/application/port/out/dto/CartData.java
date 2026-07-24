package com.ecoapi.techstore.order.application.port.out.dto;

import java.util.List;

/**
 * Data Transfer Object for cart information
 * Used for inter-context communication between Order and Cart contexts
 * This decouples Order from Cart's internal domain model
 */
public record CartData(
        Long cartId,
        Long userId,
        List<CartItemData> items
) {
    public CartData {
        if (cartId == null) {
            throw new IllegalArgumentException("Cart ID cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (items == null) {
            throw new IllegalArgumentException("Items list cannot be null");
        }
    }
    
    /**
     * Check if cart is empty
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    /**
     * Get total number of items in cart
     */
    public int totalItems() {
        return items.stream()
                .mapToInt(CartItemData::quantity)
                .sum();
    }
}
