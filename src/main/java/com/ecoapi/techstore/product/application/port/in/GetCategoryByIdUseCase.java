package com.ecoapi.techstore.product.application.port.in;

import com.ecoapi.techstore.product.domain.model.Category;

/**
 * Use case for getting a category by ID
 */
public interface GetCategoryByIdUseCase {
    Category execute(Long id);
}
