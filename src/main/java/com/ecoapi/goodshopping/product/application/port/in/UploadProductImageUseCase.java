package com.ecoapi.goodshopping.product.application.port.in;

import com.ecoapi.goodshopping.product.application.port.out.ImageFile;
import com.ecoapi.goodshopping.product.domain.model.Product;

/**
 * Use case for uploading product images
 * Uses domain abstraction ImageFile instead of framework-specific MultipartFile
 */
public interface UploadProductImageUseCase {
    
    /**
     * Upload an image for a product
     * @param productId The ID of the product
     * @param imageFile The image file to upload (domain abstraction)
     * @return The updated Product with the image URL
     */
    Product uploadImage(Long productId, ImageFile imageFile);
}
