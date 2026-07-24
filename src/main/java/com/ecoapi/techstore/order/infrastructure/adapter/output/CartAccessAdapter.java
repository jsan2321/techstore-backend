package com.ecoapi.techstore.order.infrastructure.adapter.output;

import com.ecoapi.techstore.cart.application.port.in.ClearCartUseCase;
import com.ecoapi.techstore.cart.application.port.in.GetCartUseCase;
import com.ecoapi.techstore.cart.application.port.out.ProductAccessPort;
import com.ecoapi.techstore.cart.application.port.out.dto.ProductData;
import com.ecoapi.techstore.cart.application.service.dto.ClearCartCommand;
import com.ecoapi.techstore.cart.application.service.dto.GetCartQuery;
import com.ecoapi.techstore.cart.domain.model.Cart;
import com.ecoapi.techstore.cart.domain.model.CartItem;
import com.ecoapi.techstore.order.application.port.out.CartAccessPort;
import com.ecoapi.techstore.order.application.port.out.dto.CartData;
import com.ecoapi.techstore.order.application.port.out.dto.CartItemData;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Infrastructure Adapter for Cart context access
 * 
 * This adapter implements Order's CartAccessPort by delegating to Cart's input ports (use cases).
 * It acts as an Anti-Corruption Layer (ACL), translating between Order's DTOs and Cart's domain model.
 * 
 * The adapter lives in Order's infrastructure layer because:
 * 1. It implements an Order port (CartAccessPort)
 * 2. It knows about Cart's use cases (infrastructure concern)
 * 3. It translates between contexts (ACL pattern)
 */
public class CartAccessAdapter implements CartAccessPort {
    
    private final GetCartUseCase getCartUseCase;
    private final ClearCartUseCase clearCartUseCase;
    private final ProductAccessPort productAccessPort;
    
    public CartAccessAdapter(GetCartUseCase getCartUseCase, 
                            ClearCartUseCase clearCartUseCase,
                            ProductAccessPort productAccessPort) {
        this.getCartUseCase = getCartUseCase;
        this.clearCartUseCase = clearCartUseCase;
        this.productAccessPort = productAccessPort;
    }
    
    @Override
    public CartData getCartForUser(Long userId) {
        // Call Cart's input port (use case)
        Cart cart = getCartUseCase.getCart(GetCartQuery.forUser(userId));
        
        // Translate Cart's domain model to Order's DTO (Anti-Corruption Layer)
        return toCartData(cart);
    }
    
    @Override
    public void clearCart(Long cartId) {
        // Call Cart's input port (use case)
        clearCartUseCase.clearCart(ClearCartCommand.forCart(cartId));
    }
    
    /**
     * Translate Cart domain model to Order's CartData DTO
     * This is the Anti-Corruption Layer translation
     */
    private CartData toCartData(Cart cart) {
        List<CartItemData> items = cart.getItems().stream()
                .map(this::toCartItemData)
                .collect(Collectors.toList());
        
        return new CartData(
                cart.getId().value(),
                cart.getUserId().value(),
                items
        );
    }
    
    /**
     * Translate CartItem domain model to Order's CartItemData DTO
     */
    private CartItemData toCartItemData(CartItem cartItem) {
        Long productId = cartItem.getProductId().value();
        ProductData productData = productAccessPort.getProductById(productId)
                .orElseThrow(() -> new IllegalStateException("Product not found or inactive: " + productId));

        return new CartItemData(
                productId,
                cartItem.getProductName(),
                cartItem.getProductDescription(),
                cartItem.getProductImageUrl(),
                cartItem.getQuantity(),
                productData.effectivePrice().value()
        );
    }
}
