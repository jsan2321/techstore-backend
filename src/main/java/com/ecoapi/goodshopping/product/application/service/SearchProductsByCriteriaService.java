package com.ecoapi.goodshopping.product.application.service;

import com.ecoapi.goodshopping.product.application.port.in.SearchProductsByCriteriaUseCase;
import com.ecoapi.goodshopping.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.goodshopping.product.application.service.dto.ProductReadModel;
import com.ecoapi.goodshopping.product.domain.model.ProductSearchCriteria;

import java.util.List;

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
    public List<ProductReadModel> search(ProductSearchCriteria criteria) {
        // If no criteria provided, return all
        if (criteria.isEmpty()) {
            return productRepository.findAllAsReadModel();
        }
        
        // Apply criteria-based search
        return productRepository.searchByCriteria(criteria);
    }
}
