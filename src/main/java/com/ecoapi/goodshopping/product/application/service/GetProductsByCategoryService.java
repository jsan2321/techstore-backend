package com.ecoapi.goodshopping.product.application.service;

import com.ecoapi.goodshopping.product.application.port.in.GetProductsByCategoryUseCase;
import com.ecoapi.goodshopping.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.goodshopping.product.domain.model.Product;

import java.util.List;

/**
 * Application Service for getting products by category
 * Single Responsibility: Handle product retrieval by category
 */
public class GetProductsByCategoryService implements GetProductsByCategoryUseCase {
    
    private final ProductRepositoryPort productRepository;
    
    public GetProductsByCategoryService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public List<Product> execute(String category) {
        return productRepository.findByCategory(category);
    }
}
