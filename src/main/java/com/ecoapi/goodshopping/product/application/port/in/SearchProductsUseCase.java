package com.ecoapi.goodshopping.product.application.port.in;

import com.ecoapi.goodshopping.product.domain.model.Product;

import java.util.List;

/**
 * Input Port (Use Case) for searching products
 * Combines multiple search criteria
 * This defines WHAT the application can do, not HOW
 */
public interface SearchProductsUseCase {
    
    List<Product> byCategoryAndBrand(String category, String brand);
    
    List<Product> byName(String name);
    
    List<Product> byBrandAndName(String brand, String name);
}
