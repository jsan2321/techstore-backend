package com.ecoapi.techstore.product.application.port.in;

import com.ecoapi.techstore.common.application.dto.PagedResult;
import com.ecoapi.techstore.product.application.service.dto.ProductReadModel;
import com.ecoapi.techstore.product.application.service.dto.ProductSearchCriteria;

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
    PagedResult<ProductReadModel> search(ProductSearchCriteria criteria);
}
