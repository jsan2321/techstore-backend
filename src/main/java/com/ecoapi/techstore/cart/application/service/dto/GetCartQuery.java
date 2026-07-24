package com.ecoapi.techstore.cart.application.service.dto;

/**
 * Query for retrieving a cart
 * Supports operations by userId (user's own cart) or cartId (admin operations)
 */
public record GetCartQuery(
    Long userId,
    Long cartId
) {
    public GetCartQuery {
        if (userId == null && cartId == null) {
            throw new IllegalArgumentException("Either userId or cartId must be provided");
        }
        if (userId != null && cartId != null) {
            throw new IllegalArgumentException("Only one of userId or cartId should be provided");
        }
    }
    
    /**
     * Create query for user's cart (user-facing operations)
     */
    public static GetCartQuery forUser(Long userId) {
        return new GetCartQuery(userId, null);
    }
    
    /**
     * Create query for specific cart (admin operations)
     */
    public static GetCartQuery forCart(Long cartId) {
        return new GetCartQuery(null, cartId);
    }
}
