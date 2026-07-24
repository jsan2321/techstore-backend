package com.ecoapi.techstore.product.infrastructure.adapter.input.rest.response;

import com.ecoapi.techstore.product.domain.model.Category;

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
