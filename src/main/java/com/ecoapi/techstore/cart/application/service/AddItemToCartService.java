package com.ecoapi.techstore.cart.application.service;

import com.ecoapi.techstore.cart.application.port.in.AddItemToCartUseCase;
import com.ecoapi.techstore.cart.application.port.out.CartEventPublisherPort;
import com.ecoapi.techstore.cart.application.port.out.CartRepositoryPort;
import com.ecoapi.techstore.cart.application.port.out.ProductAccessPort;
import com.ecoapi.techstore.cart.application.port.out.dto.ProductData;
import com.ecoapi.techstore.cart.application.service.dto.AddItemCommand;
import com.ecoapi.techstore.cart.domain.events.ItemAddedToCartEvent;
import com.ecoapi.techstore.cart.domain.exception.CartNotFoundException;
import com.ecoapi.techstore.cart.domain.exception.ProductNotFoundException;
import com.ecoapi.techstore.cart.domain.model.Cart;
import com.ecoapi.techstore.cart.domain.model.CartId;
import com.ecoapi.techstore.cart.domain.model.CartItem;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;

/**
 * Application Service for adding items to cart
 * Single Responsibility: Handle add item to cart business logic
 * 
 * Uses ProductAccessPort (ACL) to access Product bounded context
 * instead of directly depending on Product's use cases.
 */
public class AddItemToCartService implements AddItemToCartUseCase {
    
    private final CartRepositoryPort cartRepository;
    private final ProductAccessPort productAccessPort;
    private final CartEventPublisherPort eventPublisher;
    
    public AddItemToCartService(CartRepositoryPort cartRepository,
                                ProductAccessPort productAccessPort,
                                CartEventPublisherPort eventPublisher) {
        this.cartRepository = cartRepository;
        this.productAccessPort = productAccessPort;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public Cart addItem(AddItemCommand command) {
        // Determine cart based on command
        Cart cart;
        if (command.userId() != null) {
            // Get or create cart for user
            cart = cartRepository.findByUserId(UserId.of(command.userId()))
                    .orElseGet(() -> {
                        Cart newCart = Cart.createFor(UserId.of(command.userId()));
                        return cartRepository.save(newCart);
                    });
        } else {
            // Get cart by cartId
            cart = cartRepository.findById(CartId.of(command.cartId()))
                    .orElseThrow(() -> new CartNotFoundException("Cart not found with id: " + command.cartId()));
        }
        
        Long cartId = cart.getId().value();
        
        // Use ACL to access Product context
        ProductData product = productAccessPort.getProductById(command.productId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + command.productId()));
        
        CartItem cartItem = new CartItem(
                ProductId.of(command.productId()),
                product.name(),
                product.description(),
            product.imageUrl(),
                command.quantity(),
            product.effectivePrice()
        );
        
        cart.addItem(cartItem);
        Cart savedCart = cartRepository.save(cart);
        
        // Publish domain event
        ItemAddedToCartEvent event = new ItemAddedToCartEvent(
                CartId.of(cartId),
                ProductId.of(command.productId()),
                product.name(),
                command.quantity()
        );
        eventPublisher.publish(event);
        
        return savedCart;
    }
}
