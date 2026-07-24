package com.ecoapi.techstore.order.application.service.dto;

/**
 * Query for retrieving orders
 * Supports operations by orderId, userId, or all orders
 */
public record GetOrderQuery(
    Long orderId,
    Long userId,
    boolean fetchAll
) {
    public GetOrderQuery {
        if (!fetchAll && orderId == null && userId == null) {
            throw new IllegalArgumentException("Either orderId, userId must be provided, or fetchAll must be true");
        }
        if (fetchAll && (orderId != null || userId != null)) {
            throw new IllegalArgumentException("When fetchAll is true, orderId and userId must be null");
        }
        if (orderId != null && userId != null) {
            throw new IllegalArgumentException("Only one of orderId or userId should be provided");
        }
    }
    
    /**
     * Create query for a specific order
     */
    public static GetOrderQuery forOrder(Long orderId) {
        return new GetOrderQuery(orderId, null, false);
    }
    
    /**
     * Create query for user's orders
     */
    public static GetOrderQuery forUser(Long userId) {
        return new GetOrderQuery(null, userId, false);
    }
    
    /**
     * Create query for all orders (admin use)
     */
    public static GetOrderQuery forAll() {
        return new GetOrderQuery(null, null, true);
    }
}
