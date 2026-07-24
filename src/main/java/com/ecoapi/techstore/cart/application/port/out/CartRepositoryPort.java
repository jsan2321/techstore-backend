package com.ecoapi.techstore.cart.application.port.out;

import com.ecoapi.techstore.cart.domain.model.Cart;
import com.ecoapi.techstore.cart.domain.model.CartId;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;

import java.util.Optional;

public interface CartRepositoryPort {
    Cart save(Cart cart);
    Optional<Cart> findById(CartId cartId);
    Optional<Cart> findByUserId(UserId userId);
    void deleteById(CartId cartId);
    boolean existsById(CartId cartId);
}
