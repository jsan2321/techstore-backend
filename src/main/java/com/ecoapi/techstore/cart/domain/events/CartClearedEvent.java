package com.ecoapi.techstore.cart.domain.events;

import com.ecoapi.techstore.cart.domain.model.CartId;
import com.ecoapi.techstore.common.domain.events.DomainEvent;

import java.time.LocalDateTime;

public record CartClearedEvent(
    CartId cartId,
    LocalDateTime occurredOn
) implements DomainEvent {
    
    public CartClearedEvent(CartId cartId) {
        this(cartId, LocalDateTime.now());
    }
}
