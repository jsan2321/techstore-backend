package com.ecoapi.techstore.cart.application.port.in;

import com.ecoapi.techstore.cart.application.service.dto.UpdateItemQuantityCommand;
import com.ecoapi.techstore.cart.domain.model.Cart;

public interface UpdateCartItemQuantityUseCase {
    Cart updateItemQuantity(UpdateItemQuantityCommand command);
    
    @Deprecated(forRemoval = true)
    default Cart updateItemQuantity(Long cartId, Long productId, int quantity) {
        return updateItemQuantity(UpdateItemQuantityCommand.forCart(cartId, productId, quantity));
    }
    
    @Deprecated(forRemoval = true)
    default Cart updateItemQuantityByUserId(Long userId, Long productId, int quantity) {
        return updateItemQuantity(UpdateItemQuantityCommand.forUser(userId, productId, quantity));
    }
}
