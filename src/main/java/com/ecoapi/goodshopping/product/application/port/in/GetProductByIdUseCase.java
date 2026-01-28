package com.ecoapi.goodshopping.product.application.port.in;

import com.ecoapi.goodshopping.product.domain.model.Product;

/**
 * Input Port (Use Case) for getting a product by ID
 * This defines WHAT the application can do, not HOW
 */
public interface GetProductByIdUseCase {
    
    Product execute(Long id);
}
