package com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest.response;

import com.ecoapi.goodshopping.product.domain.model.Brand;

/**
 * Response DTO for Brand
 */
public record BrandResponse(
    Long id,
    String name
) {
    public static BrandResponse fromDomain(Brand brand) {
        return new BrandResponse(
            brand.getId().value(),
            brand.getName()
        );
    }
}
