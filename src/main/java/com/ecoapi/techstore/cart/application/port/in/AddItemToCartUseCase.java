package com.ecoapi.techstore.cart.application.port.in;

import com.ecoapi.techstore.cart.application.service.dto.AddItemCommand;
import com.ecoapi.techstore.cart.domain.model.Cart;

public interface AddItemToCartUseCase {
    Cart addItem(AddItemCommand command);
    
    @Deprecated(forRemoval = true)
    default Cart addItem(Long cartId, Long productId, int quantity) {
        return addItem(AddItemCommand.forCart(cartId, productId, quantity));
    }
    
    @Deprecated(forRemoval = true)
    default Cart addItemByUserId(Long userId, Long productId, int quantity) {
        return addItem(AddItemCommand.forUser(userId, productId, quantity));
    }
}
