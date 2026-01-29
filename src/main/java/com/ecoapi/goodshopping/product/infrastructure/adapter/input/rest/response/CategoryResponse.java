package com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest.response;

import com.ecoapi.goodshopping.product.domain.model.Category;

/**
 * Response DTO for Category
 */
public record CategoryResponse(
    Long id,
    String name
) {
    public static CategoryResponse fromDomain(Category category) {
        return new CategoryResponse(
            category.getId().value(),
            category.getName()
        );
    }
}
