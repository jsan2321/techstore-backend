package com.ecoapi.goodshopping.product.application.port.in;

import com.ecoapi.goodshopping.product.domain.model.Category;

/**
 * Use case for updating a category
 */
public interface UpdateCategoryUseCase {
    Category updateCategory(Long id, String name);
}
