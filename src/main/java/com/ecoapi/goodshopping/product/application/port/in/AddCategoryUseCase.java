package com.ecoapi.goodshopping.product.application.port.in;

import com.ecoapi.goodshopping.product.domain.model.Category;

/**
 * Use case for adding a new category
 */
public interface AddCategoryUseCase {
    Category addCategory(String name);
}
