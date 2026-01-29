package com.ecoapi.goodshopping.product.application.port.in;

import com.ecoapi.goodshopping.product.domain.model.Category;

/**
 * Use case for getting a category by ID
 */
public interface GetCategoryByIdUseCase {
    Category execute(Long id);
}
