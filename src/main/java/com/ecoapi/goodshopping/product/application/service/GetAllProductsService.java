package com.ecoapi.goodshopping.product.application.service;

import com.ecoapi.goodshopping.product.application.port.in.GetAllProductsUseCase;
import com.ecoapi.goodshopping.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.goodshopping.product.domain.model.Product;

import java.util.List;

/**
 * Application Service for getting all products
 * Single Responsibility: Handle retrieval of all products
 */
public class GetAllProductsService implements GetAllProductsUseCase {
    
    private final ProductRepositoryPort productRepository;
    
    public GetAllProductsService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public List<Product> execute() {
        return productRepository.findAll();
    }
}
