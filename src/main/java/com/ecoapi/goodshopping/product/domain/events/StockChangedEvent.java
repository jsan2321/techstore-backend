package com.ecoapi.goodshopping.product.domain.events;

import com.ecoapi.goodshopping.common.domain.events.DomainEvent;
import com.ecoapi.goodshopping.product.domain.model.ProductId;

import java.time.LocalDateTime;

/**
 * Domain event fired when product stock changes
 */
public record StockChangedEvent(
    ProductId productId,
    String productName,
    Integer previousStock,
    Integer currentStock,
    String changeType,  // "ADDED" or "REDUCED"
    LocalDateTime occurredOn
) implements DomainEvent {
    
    public StockChangedEvent(ProductId productId, String productName, 
                           Integer previousStock, Integer currentStock, String changeType) {
        this(productId, productName, previousStock, currentStock, changeType, LocalDateTime.now());
    }
}
