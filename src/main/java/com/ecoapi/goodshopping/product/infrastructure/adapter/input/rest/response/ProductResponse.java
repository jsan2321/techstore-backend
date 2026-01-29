package com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest.response;

import com.ecoapi.goodshopping.product.domain.model.Product;

import java.math.BigDecimal;

public record ProductResponse(
    Long id,
    String name,
    String brand,
    BigDecimal price,
    int inventory,
    String description,
    String categoryName,
    String imageUrl
) {
    public static ProductResponse fromDomain(Product product) {
        return new ProductResponse(
            product.getId() != null ? product.getId().value() : null,
            product.getName(),
            product.getBrand().getName(),
            product.getPrice().value(),
            product.getInventory(),
            product.getDescription(),
            product.getCategory().getName(),
            product.getImageUrl() != null ? product.getImageUrl().value() : null
        );
    }
}
