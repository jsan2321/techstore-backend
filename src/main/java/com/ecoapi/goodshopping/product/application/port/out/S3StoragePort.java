package com.ecoapi.goodshopping.product.application.port.out;

import com.ecoapi.goodshopping.product.domain.model.ImageUrl;
import org.springframework.web.multipart.MultipartFile;

/**
 * Port for S3 storage operations
 * Defines the contract for image storage in the application layer
 */
public interface S3StoragePort {
    
    /**
     * Upload an image file to S3
     * @param file The image file to upload
     * @param productId The product identifier for naming the file
     * @return The URL of the uploaded image
     */
    ImageUrl uploadImage(MultipartFile file, Long productId);
    
    /**
     * Delete an image from S3
     * @param imageUrl The URL of the image to delete
     */
    void deleteImage(ImageUrl imageUrl);
    
    /**
     * Generate a presigned URL for temporary access to an image
     * @param imageUrl The URL of the image
     * @return A presigned URL for temporary access
     */
    String generatePresignedUrl(ImageUrl imageUrl);
}
