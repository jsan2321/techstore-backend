package com.ecoapi.techstore.order.application.port.out;

import com.ecoapi.techstore.order.application.port.out.dto.CartData;

/**
 * Output Port for accessing Cart context
 * This port defines what Order needs from Cart, without depending on Cart's internals
 * 
 * In Hexagonal Architecture, this port is implemented by an adapter in the infrastructure layer
 * that translates between Order's needs and Cart's input ports (use cases)
 */
public interface CartAccessPort {
    
    /**
     * Get cart data for a specific user
     * 
     * @param userId the user's ID
     * @return cart data including items, or empty cart if user has no cart
     */
    CartData getCartForUser(Long userId);
    
    /**
     * Clear all items from a user's cart
     * Typically called after order is successfully placed
     * 
     * @param cartId the cart's ID to clear
     */
    void clearCart(Long cartId);
}
