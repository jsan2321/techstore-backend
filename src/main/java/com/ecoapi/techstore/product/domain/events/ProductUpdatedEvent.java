package com.ecoapi.techstore.product.domain.events;

import com.ecoapi.techstore.common.domain.events.DomainEvent;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;

import java.time.LocalDateTime;

/**
 * Domain event fired when a product is updated
 */
public record ProductUpdatedEvent(
    ProductId productId,
    String name,
    String brand,
    String category,
    LocalDateTime occurredOn
) implements DomainEvent {
    
    public ProductUpdatedEvent(ProductId productId, String name, String brand, String category) {
        this(productId, name, brand, category, LocalDateTime.now());
    }
}
