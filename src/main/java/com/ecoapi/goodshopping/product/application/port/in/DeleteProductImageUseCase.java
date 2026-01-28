package com.ecoapi.goodshopping.product.application.port.in;

import com.ecoapi.goodshopping.product.domain.model.Product;

/**
 * Use case for deleting product images
 */
public interface DeleteProductImageUseCase {
    
    /**
     * Delete the image for a product
     * @param productId The ID of the product
     * @return The updated Product without the image URL
     */
    Product deleteImage(Long productId);
}
