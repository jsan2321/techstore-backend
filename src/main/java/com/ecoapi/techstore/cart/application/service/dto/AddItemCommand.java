package com.ecoapi.techstore.cart.application.service.dto;

/**
 * Command for adding an item to cart
 * Supports operations by userId (user's own cart) or cartId (admin operations)
 */
public record AddItemCommand(
    Long userId,
    Long cartId,
    Long productId,
    int quantity
) {
    public AddItemCommand {
        if (userId == null && cartId == null) {
            throw new IllegalArgumentException("Either userId or cartId must be provided");
        }
        if (userId != null && cartId != null) {
            throw new IllegalArgumentException("Only one of userId or cartId should be provided");
        }
        if (productId == null) {
            throw new IllegalArgumentException("Product ID is required");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }
    
    /**
     * Create command for user's cart (user-facing operations)
     */
    public static AddItemCommand forUser(Long userId, Long productId, int quantity) {
        return new AddItemCommand(userId, null, productId, quantity);
    }
    
    /**
     * Create command for specific cart (admin operations)
     */
    public static AddItemCommand forCart(Long cartId, Long productId, int quantity) {
        return new AddItemCommand(null, cartId, productId, quantity);
    }
}
