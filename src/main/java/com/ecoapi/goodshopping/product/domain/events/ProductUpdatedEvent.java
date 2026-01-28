package com.ecoapi.goodshopping.product.domain.events;

import com.ecoapi.goodshopping.common.domain.events.DomainEvent;
import com.ecoapi.goodshopping.product.domain.model.ProductId;

import java.time.LocalDateTime;

/**
 * Domain event fired when a product is updated
 */
public record ProductUpdatedEvent(
    ProductId productId,
    String name,
    String brand,
    LocalDateTime occurredOn
) implements DomainEvent {
    
    public ProductUpdatedEvent(ProductId productId, String name, String brand) {
        this(productId, name, brand, LocalDateTime.now());
    }
}
