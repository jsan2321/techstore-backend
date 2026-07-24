package com.ecoapi.techstore.product.application.service;

import com.ecoapi.techstore.product.application.port.in.DeleteProductUseCase;
import com.ecoapi.techstore.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.techstore.product.domain.exception.ProductNotFoundException;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;

/**
 * Application Service for deleting products
 * Single Responsibility: Handle product deletion business logic
 */
public class DeleteProductService implements DeleteProductUseCase {
    
    private final ProductRepositoryPort productRepository;
    
    public DeleteProductService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public void deleteProduct(Long id) {
        ProductId productId = ProductId.of(id);
        
        if (!productRepository.findById(productId).isPresent()) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        
        productRepository.deleteById(productId);
    }
}
