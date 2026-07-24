package com.ecoapi.techstore.cart.application.service;

import com.ecoapi.techstore.cart.application.port.in.ClearCartUseCase;
import com.ecoapi.techstore.cart.application.port.out.CartEventPublisherPort;
import com.ecoapi.techstore.cart.application.port.out.CartRepositoryPort;
import com.ecoapi.techstore.cart.application.service.dto.ClearCartCommand;
import com.ecoapi.techstore.cart.domain.events.CartClearedEvent;
import com.ecoapi.techstore.cart.domain.exception.CartNotFoundException;
import com.ecoapi.techstore.cart.domain.model.Cart;
import com.ecoapi.techstore.cart.domain.model.CartId;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;

/**
 * Application Service for clearing cart
 * Single Responsibility: Handle clear cart business logic
 */
public class ClearCartService implements ClearCartUseCase {
    
    private final CartRepositoryPort cartRepository;
    private final CartEventPublisherPort eventPublisher;
    
    public ClearCartService(CartRepositoryPort cartRepository,
                           CartEventPublisherPort eventPublisher) {
        this.cartRepository = cartRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public void clearCart(ClearCartCommand command) {
        // Determine cart based on command
        Cart cart;
        if (command.userId() != null) {
            // Get cart for user (no auto-create for clear)
            cart = cartRepository.findByUserId(UserId.of(command.userId()))
                    .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + command.userId()));
        } else {
            // Get cart by cartId
            cart = cartRepository.findById(CartId.of(command.cartId()))
                    .orElseThrow(() -> new CartNotFoundException("Cart not found with id: " + command.cartId()));
        }
        
        Long cartId = cart.getId().value();
        cart.clear();
        cartRepository.save(cart);
        
        // Publish domain event
        CartClearedEvent event = new CartClearedEvent(CartId.of(cartId));
        eventPublisher.publish(event);
    }
}
