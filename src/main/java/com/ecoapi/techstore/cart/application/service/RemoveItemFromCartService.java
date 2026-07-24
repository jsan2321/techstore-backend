package com.ecoapi.techstore.cart.application.service;

import com.ecoapi.techstore.cart.application.port.in.RemoveItemFromCartUseCase;
import com.ecoapi.techstore.cart.application.port.out.CartEventPublisherPort;
import com.ecoapi.techstore.cart.application.port.out.CartRepositoryPort;
import com.ecoapi.techstore.cart.application.service.dto.RemoveItemCommand;
import com.ecoapi.techstore.cart.domain.events.ItemRemovedFromCartEvent;
import com.ecoapi.techstore.cart.domain.exception.CartNotFoundException;
import com.ecoapi.techstore.cart.domain.model.Cart;
import com.ecoapi.techstore.cart.domain.model.CartId;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;

/**
 * Application Service for removing items from cart
 * Single Responsibility: Handle remove item from cart business logic
 */
public class RemoveItemFromCartService implements RemoveItemFromCartUseCase {
    
    private final CartRepositoryPort cartRepository;
    private final CartEventPublisherPort eventPublisher;
    
    public RemoveItemFromCartService(CartRepositoryPort cartRepository,
                                     CartEventPublisherPort eventPublisher) {
        this.cartRepository = cartRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public Cart removeItem(RemoveItemCommand command) {
        // Determine cart based on command
        Cart cart;
        if (command.userId() != null) {
            // Get cart for user (no auto-create for removal)
            cart = cartRepository.findByUserId(UserId.of(command.userId()))
                    .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + command.userId()));
        } else {
            // Get cart by cartId
            cart = cartRepository.findById(CartId.of(command.cartId()))
                    .orElseThrow(() -> new CartNotFoundException("Cart not found with id: " + command.cartId()));
        }
        
        Long cartId = cart.getId().value();
        cart.removeItem(ProductId.of(command.productId()));
        Cart savedCart = cartRepository.save(cart);
        
        // Publish domain event
        ItemRemovedFromCartEvent event = new ItemRemovedFromCartEvent(
                CartId.of(cartId),
                ProductId.of(command.productId())
        );
        eventPublisher.publish(event);
        
        return savedCart;
    }
}
