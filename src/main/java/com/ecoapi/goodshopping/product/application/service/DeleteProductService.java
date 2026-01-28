package com.ecoapi.goodshopping.product.application.service;

import com.ecoapi.goodshopping.product.application.port.in.DeleteProductUseCase;
import com.ecoapi.goodshopping.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.goodshopping.product.domain.exception.ProductNotFoundException;
import com.ecoapi.goodshopping.product.domain.model.ProductId;

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
