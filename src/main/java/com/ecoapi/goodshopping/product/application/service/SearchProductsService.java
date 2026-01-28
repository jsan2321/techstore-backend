package com.ecoapi.goodshopping.product.application.service;

import com.ecoapi.goodshopping.product.application.port.in.SearchProductsUseCase;
import com.ecoapi.goodshopping.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.goodshopping.product.domain.model.Product;

import java.util.List;

/**
 * Application Service for searching products with multiple criteria
 * Single Responsibility: Handle product search operations
 */
public class SearchProductsService implements SearchProductsUseCase {
    
    private final ProductRepositoryPort productRepository;
    
    public SearchProductsService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public List<Product> byCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryAndBrand(category, brand);
    }
    
    @Override
    public List<Product> byName(String name) {
        return productRepository.findByName(name);
    }
    
    @Override
    public List<Product> byBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name);
    }
}
