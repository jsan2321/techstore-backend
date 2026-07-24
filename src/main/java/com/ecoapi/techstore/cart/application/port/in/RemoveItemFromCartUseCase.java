package com.ecoapi.techstore.cart.application.port.in;

import com.ecoapi.techstore.cart.application.service.dto.RemoveItemCommand;
import com.ecoapi.techstore.cart.domain.model.Cart;

public interface RemoveItemFromCartUseCase {
    Cart removeItem(RemoveItemCommand command);
    
    @Deprecated(forRemoval = true)
    default Cart removeItem(Long cartId, Long productId) {
        return removeItem(RemoveItemCommand.forCart(cartId, productId));
    }
    
    @Deprecated(forRemoval = true)
    default Cart removeItemByUserId(Long userId, Long productId) {
        return removeItem(RemoveItemCommand.forUser(userId, productId));
    }
}
