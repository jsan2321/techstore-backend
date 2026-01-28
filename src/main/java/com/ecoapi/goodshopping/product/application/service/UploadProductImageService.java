package com.ecoapi.goodshopping.product.application.service;

import com.ecoapi.goodshopping.product.application.port.in.UploadProductImageUseCase;
import com.ecoapi.goodshopping.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.goodshopping.product.application.port.out.S3StoragePort;
import com.ecoapi.goodshopping.product.domain.model.ImageUrl;
import com.ecoapi.goodshopping.product.domain.model.Product;
import com.ecoapi.goodshopping.product.domain.model.ProductId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for uploading product images
 */
@Service
public class UploadProductImageService implements UploadProductImageUseCase {
    
    private final ProductRepositoryPort productRepository;
    private final S3StoragePort s3Storage;
    
    public UploadProductImageService(ProductRepositoryPort productRepository, 
                                    S3StoragePort s3Storage) {
        this.productRepository = productRepository;
        this.s3Storage = s3Storage;
    }
    
    @Override
    @Transactional
    public Product uploadImage(Long productId, MultipartFile imageFile) {
        // Find the product
        Product product = productRepository.findById(ProductId.of(productId))
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        // Delete old image if exists
        if (product.getImageUrl() != null) {
            try {
                s3Storage.deleteImage(product.getImageUrl());
            } catch (Exception e) {
                // Log but don't fail - old image cleanup is not critical
                System.err.println("Failed to delete old image: " + e.getMessage());
            }
        }
        
        // Upload new image
        ImageUrl imageUrl = s3Storage.uploadImage(imageFile, productId);
        
        // Update product with new image URL
        product.updateImageUrl(imageUrl);
        
        // Save and return
        return productRepository.save(product);
    }
}
