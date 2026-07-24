package com.ecoapi.techstore.product.infrastructure.adapter.input.rest.response;

import com.ecoapi.techstore.product.domain.model.Product;

import java.math.BigDecimal;

/**
 * Product response DTO.
 */
public record ProductResponse(
    Long id,
    String name,
    String brand,
    BigDecimal price,
    int stock,
    String description,
    String category,
    String imageUrl,
    Integer discountPercentage
) {
    public static ProductResponse fromDomain(Product product) {
        
        return new ProductResponse(
            product.getId() != null ? product.getId().value() : null,
            product.getName(),
            product.getBrand().getName(),
            product.getPrice().value(),
            product.getStock(),
            product.getDescription(),
            product.getCategory().getName(),
            product.getImageUrl() != null ? product.getImageUrl().value() : null,
            product.getDiscountPercentage()
        );
    }
}
