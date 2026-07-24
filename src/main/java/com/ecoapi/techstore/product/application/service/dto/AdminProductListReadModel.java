package com.ecoapi.techstore.product.application.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

/**
 * Compact read model for admin product listing.
 */
public record AdminProductListReadModel(
    Long id,
    String name,
    String category,
    BigDecimal originalPrice,
    BigDecimal effectivePrice,
    int stock,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    DiscountInfo discount
) {
    public record DiscountInfo(int percentage) {}

    public static AdminProductListReadModel create(
            Long id,
            String name,
            String category,
            BigDecimal originalPrice,
            Integer discountPercentage,
            int stock) {

        DiscountInfo discountInfo = null;
        BigDecimal effectivePrice = originalPrice;

        if (discountPercentage != null && discountPercentage > 0) {
            discountInfo = new DiscountInfo(discountPercentage);
            BigDecimal discount = originalPrice.multiply(BigDecimal.valueOf(discountPercentage))
                    .divide(BigDecimal.valueOf(100));
            effectivePrice = originalPrice.subtract(discount);
        }

        return new AdminProductListReadModel(id, name, category, originalPrice, effectivePrice, stock, discountInfo);
    }
}
