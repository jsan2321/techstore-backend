package com.ecoapi.goodshopping.product.application.service;

import com.ecoapi.goodshopping.product.application.port.in.DeleteProductImageUseCase;
import com.ecoapi.goodshopping.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.goodshopping.product.application.port.out.S3StoragePort;
import com.ecoapi.goodshopping.product.domain.model.Product;
import com.ecoapi.goodshopping.product.domain.model.ProductId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for deleting product images
 */
@Service
public class DeleteProductImageService implements DeleteProductImageUseCase {
    
    private final ProductRepositoryPort productRepository;
    private final S3StoragePort s3Storage;
    
    public DeleteProductImageService(ProductRepositoryPort productRepository, 
                                    S3StoragePort s3Storage) {
        this.productRepository = productRepository;
        this.s3Storage = s3Storage;
    }
    
    @Override
    @Transactional
    public Product deleteImage(Long productId) {
        // Find the product
        Product product = productRepository.findById(ProductId.of(productId))
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        // Delete image from S3 if exists
        if (product.getImageUrl() != null) {
            s3Storage.deleteImage(product.getImageUrl());
            product.removeImage();
        }
        
        // Save and return
        return productRepository.save(product);
    }
}
