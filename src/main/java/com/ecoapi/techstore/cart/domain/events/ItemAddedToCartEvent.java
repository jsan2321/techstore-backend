package com.ecoapi.techstore.cart.domain.events;

import com.ecoapi.techstore.cart.domain.model.CartId;
import com.ecoapi.techstore.common.domain.events.DomainEvent;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;

import java.time.LocalDateTime;

public record ItemAddedToCartEvent(
    CartId cartId,
    ProductId productId,
    String productName,
    int quantity,
    LocalDateTime occurredOn
) implements DomainEvent {
    
    public ItemAddedToCartEvent(CartId cartId, ProductId productId, String productName, int quantity) {
        this(cartId, productId, productName, quantity, LocalDateTime.now());
    }
}
