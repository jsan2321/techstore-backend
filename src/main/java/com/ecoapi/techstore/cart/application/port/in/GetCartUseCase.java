package com.ecoapi.techstore.cart.application.port.in;

import com.ecoapi.techstore.cart.application.service.dto.GetCartQuery;
import com.ecoapi.techstore.cart.domain.model.Cart;

public interface GetCartUseCase {
    Cart getCart(GetCartQuery query);
    
    @Deprecated(forRemoval = true)
    default Cart getCartById(Long cartId) {
        return getCart(GetCartQuery.forCart(cartId));
    }
    
    @Deprecated(forRemoval = true)
    default Cart getCartByUserId(Long userId) {
        return getCart(GetCartQuery.forUser(userId));
    }
}
