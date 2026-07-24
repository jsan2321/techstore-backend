package com.ecoapi.techstore.product.application.service;

import com.ecoapi.techstore.product.application.port.in.DeleteProductImageUseCase;
import com.ecoapi.techstore.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.techstore.product.application.port.out.S3StoragePort;
import com.ecoapi.techstore.product.domain.model.Product;
import com.ecoapi.techstore.common.domain.exceptions.NotFoundException;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;

/**
 * Service for deleting product images
 * Framework-agnostic application service
 */
public class DeleteProductImageService implements DeleteProductImageUseCase {
    
    private final ProductRepositoryPort productRepository;
    private final S3StoragePort s3Storage;
    
    public DeleteProductImageService(ProductRepositoryPort productRepository, 
                                    S3StoragePort s3Storage) {
        this.productRepository = productRepository;
        this.s3Storage = s3Storage;
    }
    
    @Override
    public Product deleteImage(Long productId) {
        // Find the product
        Product product = productRepository.findById(ProductId.of(productId))
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + productId));
        
        // Delete image from S3 if exists
        if (product.getImageUrl() != null) {
            s3Storage.deleteImage(product.getImageUrl());
            product.removeImage();
        }
        
        // Save and return
        return productRepository.save(product);
    }
}
