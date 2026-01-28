package com.ecoapi.goodshopping.product.application.port.in;

import com.ecoapi.goodshopping.product.domain.model.Product;

import java.util.List;

/**
 * Input Port (Use Case) for getting products by brand
 * This defines WHAT the application can do, not HOW
 */
public interface GetProductsByBrandUseCase {
    
    List<Product> execute(String brand);
}
