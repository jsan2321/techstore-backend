package com.ecoapi.techstore.product.domain.events;

import com.ecoapi.techstore.common.domain.events.DomainEvent;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;

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
    Integer stock,
    String categoryName,
    LocalDateTime occurredOn
) implements DomainEvent {
    
    public ProductCreatedEvent(ProductId productId, String name, String brand, 
                              BigDecimal price, Integer stock, String categoryName) {
        this(productId, name, brand, price, stock, categoryName, LocalDateTime.now());
    }
}
