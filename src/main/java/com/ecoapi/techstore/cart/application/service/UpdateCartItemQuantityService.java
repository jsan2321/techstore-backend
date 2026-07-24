package com.ecoapi.techstore.cart.application.service;

import com.ecoapi.techstore.cart.application.port.in.UpdateCartItemQuantityUseCase;
import com.ecoapi.techstore.cart.application.port.out.CartRepositoryPort;
import com.ecoapi.techstore.cart.application.port.out.ProductAccessPort;
import com.ecoapi.techstore.cart.application.port.out.dto.ProductData;
import com.ecoapi.techstore.cart.application.service.dto.UpdateItemQuantityCommand;
import com.ecoapi.techstore.cart.domain.exception.CartNotFoundException;
import com.ecoapi.techstore.cart.domain.exception.ProductNotFoundException;
import com.ecoapi.techstore.cart.domain.model.Cart;
import com.ecoapi.techstore.cart.domain.model.CartId;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;

/**
 * Application Service for updating cart item quantity
 * Single Responsibility: Handle update cart item quantity business logic
 * 
 * Uses ProductAccessPort (ACL) to access Product bounded context
 * instead of directly depending on Product's use cases.
 */
public class UpdateCartItemQuantityService implements UpdateCartItemQuantityUseCase {
    
    private final CartRepositoryPort cartRepository;
    private final ProductAccessPort productAccessPort;
    
    public UpdateCartItemQuantityService(CartRepositoryPort cartRepository,
                                         ProductAccessPort productAccessPort) {
        this.cartRepository = cartRepository;
        this.productAccessPort = productAccessPort;
    }
    
    @Override
    public Cart updateItemQuantity(UpdateItemQuantityCommand command) {
        // Determine cart based on command
        Cart cart;
        if (command.userId() != null) {
            // Get cart for user (no auto-create for update)
            cart = cartRepository.findByUserId(UserId.of(command.userId()))
                    .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + command.userId()));
        } else {
            // Get cart by cartId
            cart = cartRepository.findById(CartId.of(command.cartId()))
                    .orElseThrow(() -> new CartNotFoundException("Cart not found with id: " + command.cartId()));
        }
        
        // Get updated product price using ACL
        ProductData product = productAccessPort.getProductById(command.productId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + command.productId()));
        
        // Update quantity and price
        cart.updateItemQuantity(ProductId.of(command.productId()), command.quantity());
        cart.updateItemPrice(ProductId.of(command.productId()), product.effectivePrice());
        
        return cartRepository.save(cart);
    }
}
