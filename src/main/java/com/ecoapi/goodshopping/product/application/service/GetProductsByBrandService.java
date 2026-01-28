package com.ecoapi.goodshopping.product.application.service;

import com.ecoapi.goodshopping.product.application.port.in.GetProductsByBrandUseCase;
import com.ecoapi.goodshopping.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.goodshopping.product.domain.model.Product;

import java.util.List;

/**
 * Application Service for getting products by brand
 * Single Responsibility: Handle product retrieval by brand
 */
public class GetProductsByBrandService implements GetProductsByBrandUseCase {
    
    private final ProductRepositoryPort productRepository;
    
    public GetProductsByBrandService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public List<Product> execute(String brand) {
        return productRepository.findByBrand(brand);
    }
}
