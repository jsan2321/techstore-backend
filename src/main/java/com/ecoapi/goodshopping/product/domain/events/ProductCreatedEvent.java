package com.ecoapi.goodshopping.product.domain.events;

import com.ecoapi.goodshopping.common.domain.events.DomainEvent;
import com.ecoapi.goodshopping.product.domain.model.ProductId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain event fired when a new product is created
 */
public record ProductCreatedEvent(
    ProductId productId,
    String name,
    String brand,
    BigDecimal price,
    Integer inventory,
    String categoryName,
    LocalDateTime occurredOn
) implements DomainEvent {
    
    public ProductCreatedEvent(ProductId productId, String name, String brand, 
                              BigDecimal price, Integer inventory, String categoryName) {
        this(productId, name, brand, price, inventory, categoryName, LocalDateTime.now());
    }
}
