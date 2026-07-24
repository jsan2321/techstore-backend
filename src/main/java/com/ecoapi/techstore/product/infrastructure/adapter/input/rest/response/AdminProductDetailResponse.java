package com.ecoapi.techstore.product.infrastructure.adapter.input.rest.response;

import com.ecoapi.techstore.product.domain.model.Product;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

/**
 * Full admin product response for create, update, and detail operations.
 */
public record AdminProductDetailResponse(
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
    public record DiscountInfo(int percentage) {}

    public static AdminProductDetailResponse fromDomain(Product product) {
        Integer discountPercentage = product.getDiscountPercentage();
        DiscountInfo discountInfo = null;
        BigDecimal originalPrice = product.getPrice().value();
        BigDecimal effectivePrice = originalPrice;

        if (discountPercentage != null && discountPercentage > 0) {
            discountInfo = new DiscountInfo(discountPercentage);
            BigDecimal discount = originalPrice.multiply(BigDecimal.valueOf(discountPercentage))
                    .divide(BigDecimal.valueOf(100));
            effectivePrice = originalPrice.subtract(discount);
        }

        return new AdminProductDetailResponse(
                product.getId() != null ? product.getId().value() : null,
                product.getName(),
                product.getBrand().getName(),
                originalPrice,
                effectivePrice,
                product.getStock(),
                product.getDescription(),
                product.getCategory().getName(),
                product.getImageUrl() != null ? product.getImageUrl().value() : null,
                product.isActive(),
                product.isFeatured(),
                discountInfo
        );
    }
}
