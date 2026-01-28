package com.ecoapi.goodshopping.product.application.port.in;

import com.ecoapi.goodshopping.product.domain.model.Product;

import java.util.List;

/**
 * Input Port (Use Case) for getting products by category
 * This defines WHAT the application can do, not HOW
 */
public interface GetProductsByCategoryUseCase {
    
    List<Product> execute(String category);
}
