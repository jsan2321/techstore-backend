package com.ecoapi.goodshopping.product.application.port.in;

import com.ecoapi.goodshopping.product.domain.model.Product;
import org.springframework.web.multipart.MultipartFile;

/**
 * Use case for uploading product images
 */
public interface UploadProductImageUseCase {
    
    /**
     * Upload an image for a product
     * @param productId The ID of the product
     * @param imageFile The image file to upload
     * @return The updated Product with the image URL
     */
    Product uploadImage(Long productId, MultipartFile imageFile);
}
