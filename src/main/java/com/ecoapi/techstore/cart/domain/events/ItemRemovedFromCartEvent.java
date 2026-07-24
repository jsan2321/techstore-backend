package com.ecoapi.techstore.cart.domain.events;

import com.ecoapi.techstore.cart.domain.model.CartId;
import com.ecoapi.techstore.common.domain.events.DomainEvent;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;

import java.time.LocalDateTime;

public record ItemRemovedFromCartEvent(
    CartId cartId,
    ProductId productId,
    LocalDateTime occurredOn
) implements DomainEvent {
    
    public ItemRemovedFromCartEvent(CartId cartId, ProductId productId) {
        this(cartId, productId, LocalDateTime.now());
    }
}
