package com.ecoapi.techstore.cart.application.service.dto;

/**
 * Command for clearing a cart
 * Supports operations by userId (user's own cart) or cartId (admin operations)
 */
public record ClearCartCommand(
    Long userId,
    Long cartId
) {
    public ClearCartCommand {
        if (userId == null && cartId == null) {
            throw new IllegalArgumentException("Either userId or cartId must be provided");
        }
        if (userId != null && cartId != null) {
            throw new IllegalArgumentException("Only one of userId or cartId should be provided");
        }
    }
    
    /**
     * Create command for user's cart (user-facing operations)
     */
    public static ClearCartCommand forUser(Long userId) {
        return new ClearCartCommand(userId, null);
    }
    
    /**
     * Create command for specific cart (admin operations)
     */
    public static ClearCartCommand forCart(Long cartId) {
        return new ClearCartCommand(null, cartId);
    }
}
