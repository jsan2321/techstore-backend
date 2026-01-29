package com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest;

import com.ecoapi.goodshopping.product.application.port.in.SearchProductsByCriteriaUseCase;
import com.ecoapi.goodshopping.product.application.service.dto.ProductReadModel;
import com.ecoapi.goodshopping.product.domain.model.ProductSearchCriteria;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for Product Search and Filtering
 * Handles all product query operations with flexible filtering
 * Uses CQRS pattern - returns read models for efficient querying
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductSearchController {
    
    private final SearchProductsByCriteriaUseCase searchProductsByCriteriaUseCase;
    
    public ProductSearchController(SearchProductsByCriteriaUseCase searchProductsByCriteriaUseCase) {
        this.searchProductsByCriteriaUseCase = searchProductsByCriteriaUseCase;
    }
    
    /**
     * Search products with optional filters
     * GET /api/v1/products?category=Electronics&brand=Samsung&minPrice=100&maxPrice=500&inStock=true
     * 
     * Query parameters are optional and can be combined freely:
     * - category: Filter by category name
     * - brand: Filter by brand name
     * - minPrice: Minimum price (inclusive)
     * - maxPrice: Maximum price (inclusive)
     * - inStock: Filter by stock availability (true/false)
     */
    @GetMapping
    public ResponseEntity<List<ProductReadModel>> searchProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock) {
        
        // Build search criteria from query parameters
        ProductSearchCriteria criteria = ProductSearchCriteria.builder()
                .category(category)
                .brand(brand)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .inStock(inStock)
                .build();
        
        // Execute search with criteria
        List<ProductReadModel> products = searchProductsByCriteriaUseCase.search(criteria);
        
        return ResponseEntity.ok(products);
    }
}
