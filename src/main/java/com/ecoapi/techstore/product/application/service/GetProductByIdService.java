package com.ecoapi.techstore.product.application.service;

import com.ecoapi.techstore.product.application.port.in.GetProductByIdUseCase;
import com.ecoapi.techstore.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.techstore.product.domain.exception.ProductNotFoundException;
import com.ecoapi.techstore.product.domain.model.Product;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;

/**
 * Application Service for getting a product by ID
 * Single Responsibility: Handle product retrieval by ID
 */
public class GetProductByIdService implements GetProductByIdUseCase {
    
    private final ProductRepositoryPort productRepository;
    
    public GetProductByIdService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public Product execute(Long id) {
        return productRepository.findById(ProductId.of(id))
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }
}
