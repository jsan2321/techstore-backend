package com.ecoapi.techstore.cart.application.port.in;

import com.ecoapi.techstore.cart.application.service.dto.ClearCartCommand;

public interface ClearCartUseCase {
    void clearCart(ClearCartCommand command);
    
    @Deprecated(forRemoval = true)
    default void clearCart(Long cartId) {
        clearCart(ClearCartCommand.forCart(cartId));
    }
    
    @Deprecated(forRemoval = true)
    default void clearCartByUserId(Long userId) {
        clearCart(ClearCartCommand.forUser(userId));
    }
}
