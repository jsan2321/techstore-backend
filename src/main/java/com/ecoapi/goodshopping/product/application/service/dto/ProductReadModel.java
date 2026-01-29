package com.ecoapi.goodshopping.product.application.service.dto;

import java.math.BigDecimal;

/**
 * Read Model for Product queries
 * Lightweight DTO optimized for read operations (CQRS pattern)
 * No business logic, just data projection
 */
public record ProductReadModel(
    Long id,
    String name,
    String brand,
    BigDecimal price,
    int inventory,
    String description,
    String categoryName,
    String imageUrl
) {
    
    public boolean isInStock() {
        return inventory > 0;
    }
}
