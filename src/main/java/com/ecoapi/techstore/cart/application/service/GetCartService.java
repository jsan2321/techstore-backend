package com.ecoapi.techstore.cart.application.service;

import com.ecoapi.techstore.cart.application.port.in.GetCartUseCase;
import com.ecoapi.techstore.cart.application.port.out.CartRepositoryPort;
import com.ecoapi.techstore.cart.application.service.dto.GetCartQuery;
import com.ecoapi.techstore.cart.domain.exception.CartNotFoundException;
import com.ecoapi.techstore.cart.domain.model.Cart;
import com.ecoapi.techstore.cart.domain.model.CartId;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;

/**
 * Application Service for retrieving carts
 * Single Responsibility: Handle cart retrieval business logic
 */
public class GetCartService implements GetCartUseCase {
    
    private final CartRepositoryPort cartRepository;
    
    public GetCartService(CartRepositoryPort cartRepository) {
        this.cartRepository = cartRepository;
    }
    
    @Override
    public Cart getCart(GetCartQuery query) {
        // Determine cart based on query
        if (query.userId() != null) {
            // Get or create cart for user
            return cartRepository.findByUserId(UserId.of(query.userId()))
                    .orElseGet(() -> {
                        // Create new cart for user if doesn't exist using factory method
                        Cart newCart = Cart.createFor(UserId.of(query.userId()));
                        return cartRepository.save(newCart);
                    });
        } else {
            // Get cart by cartId
            return cartRepository.findById(CartId.of(query.cartId()))
                    .orElseThrow(() -> new CartNotFoundException("Cart not found with id: " + query.cartId()));
        }
    }
}
