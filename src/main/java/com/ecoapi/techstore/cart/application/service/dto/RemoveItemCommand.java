package com.ecoapi.techstore.cart.application.service.dto;

/**
 * Command for removing an item from cart
 * Supports operations by userId (user's own cart) or cartId (admin operations)
 */
public record RemoveItemCommand(
    Long userId,
    Long cartId,
    Long productId
) {
    public RemoveItemCommand {
        if (userId == null && cartId == null) {
            throw new IllegalArgumentException("Either userId or cartId must be provided");
        }
        if (userId != null && cartId != null) {
            throw new IllegalArgumentException("Only one of userId or cartId should be provided");
        }
        if (productId == null) {
            throw new IllegalArgumentException("Product ID is required");
        }
    }
    
    /**
     * Create command for user's cart (user-facing operations)
     */
    public static RemoveItemCommand forUser(Long userId, Long productId) {
        return new RemoveItemCommand(userId, null, productId);
    }
    
    /**
     * Create command for specific cart (admin operations)
     */
    public static RemoveItemCommand forCart(Long cartId, Long productId) {
        return new RemoveItemCommand(null, cartId, productId);
    }
}
