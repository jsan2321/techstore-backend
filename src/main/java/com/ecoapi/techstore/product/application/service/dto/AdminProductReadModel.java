package com.ecoapi.techstore.product.application.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

/**
 * Admin Read Model for Product queries
 * Includes admin-specific fields like active status and featured flag
 * CQRS pattern - optimized for admin dashboard views
 */
public record AdminProductReadModel(
    Long id,
    String name,
    String brand,
    BigDecimal originalPrice,
    BigDecimal effectivePrice,
    int stock,
    String description,
    String category,
    String imageUrl,
    boolean active,
    boolean featured,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    DiscountInfo discount
) {
    public record DiscountInfo(
        int percentage
    ) {}

    public static AdminProductReadModel create(
            Long id, String name, String brand, BigDecimal price,
            Integer discountPercentage, int stock, String description,
            String category, String imageUrl, boolean active, boolean featured) {

        DiscountInfo discountInfo = null;
        BigDecimal effectivePrice = price;

        if (discountPercentage != null && discountPercentage > 0) {
            discountInfo = new DiscountInfo(discountPercentage);
            BigDecimal discount = price.multiply(BigDecimal.valueOf(discountPercentage))
                    .divide(BigDecimal.valueOf(100));
            effectivePrice = price.subtract(discount);
        }

        return new AdminProductReadModel(id, name, brand, price, effectivePrice, stock,
                description, category, imageUrl, active, featured, discountInfo);
    }
}
