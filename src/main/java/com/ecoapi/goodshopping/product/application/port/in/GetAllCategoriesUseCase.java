package com.ecoapi.goodshopping.product.application.port.in;

import com.ecoapi.goodshopping.product.domain.model.Category;
import java.util.List;

/**
 * Use case for getting all categories
 */
public interface GetAllCategoriesUseCase {
    List<Category> execute();
}
