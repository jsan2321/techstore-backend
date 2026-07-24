package com.ecoapi.techstore.product.application.port.in;

import com.ecoapi.techstore.product.domain.model.Category;

/**
 * Use case for updating a category
 */
public interface UpdateCategoryUseCase {
    Category updateCategory(Long id, String name);
}
