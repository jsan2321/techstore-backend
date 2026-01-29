package com.ecoapi.goodshopping.product.application.port.in;

import com.ecoapi.goodshopping.product.application.service.dto.ProductReadModel;
import com.ecoapi.goodshopping.product.domain.model.ProductSearchCriteria;

import java.util.List;

/**
 * Use case for searching products by criteria
 * Returns lightweight read models instead of full domain entities (CQRS)
 */
public interface SearchProductsByCriteriaUseCase {
    
    /**
     * Search products using flexible criteria
     * @param criteria Search criteria with optional filters
     * @return List of product read models
     */
    List<ProductReadModel> search(ProductSearchCriteria criteria);
}
