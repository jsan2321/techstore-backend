package com.ecoapi.techstore.product.application.port.in;

import com.ecoapi.techstore.product.domain.model.Category;
import java.util.List;

/**
 * Use case for getting all categories
 */
public interface GetAllCategoriesUseCase {
    List<Category> execute();
}
