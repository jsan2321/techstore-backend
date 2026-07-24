package com.ecoapi.techstore.product.application.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

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
    BigDecimal originalPrice,
    BigDecimal effectivePrice,
    int stock,
    String description,
    String category,
    String imageUrl,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    DiscountInfo discount
) {
    /**
     * Nested record containing discount-related information.
     * Only present when a discount is applied to the product.
     * Note: effectivePrice already shows the discounted price, so no salePrice here.
     */
    public record DiscountInfo(
        int percentage
    ) {}
    
    /**
     * Factory method to create ProductReadModel with discount calculation
     */
    public static ProductReadModel create(
            Long id, String name, String brand, BigDecimal price,
            Integer discountPercentage,
            int stock, String description, String category, String imageUrl) {
        
        DiscountInfo discountInfo = null;
        BigDecimal effectivePrice = price;

        if (discountPercentage != null && discountPercentage > 0) {
            discountInfo = new DiscountInfo(discountPercentage);
            BigDecimal discount = price.multiply(BigDecimal.valueOf(discountPercentage))
                    .divide(BigDecimal.valueOf(100));
            effectivePrice = price.subtract(discount);
        }

        return new ProductReadModel(id, name, brand, price, effectivePrice, stock,
                description, category, imageUrl, discountInfo);
    }
}
