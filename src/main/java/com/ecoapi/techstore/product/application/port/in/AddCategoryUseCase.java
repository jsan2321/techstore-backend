package com.ecoapi.techstore.product.application.port.in;

import com.ecoapi.techstore.product.domain.model.Category;

/**
 * Use case for adding a new category
 */
public interface AddCategoryUseCase {
    Category addCategory(String name);
}
