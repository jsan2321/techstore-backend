package com.ecoapi.techstore.product.application.service;

import com.ecoapi.techstore.common.application.dto.PagedResult;
import com.ecoapi.techstore.product.application.port.in.SearchProductsByCriteriaUseCase;
import com.ecoapi.techstore.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.techstore.product.application.service.dto.ProductReadModel;
import com.ecoapi.techstore.product.application.service.dto.ProductSearchCriteria;

/**
 * Service for searching products by criteria
 * Uses CQRS pattern - returns read models instead of full domain entities
 * Framework-agnostic application service
 */
public class SearchProductsByCriteriaService implements SearchProductsByCriteriaUseCase {
    
    private final ProductRepositoryPort productRepository;
    
    public SearchProductsByCriteriaService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public PagedResult<ProductReadModel> search(ProductSearchCriteria criteria) {
        return productRepository.searchByCriteria(criteria);
    }
}
