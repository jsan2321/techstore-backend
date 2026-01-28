package com.ecoapi.goodshopping.product.application.port.out;

import com.ecoapi.goodshopping.product.domain.model.Product;
import com.ecoapi.goodshopping.product.domain.model.ProductId;

import java.util.List;
import java.util.Optional;

/**
 * Output Port for Product repository operations
 * Infrastructure will provide the actual implementation
 */
public interface ProductRepositoryPort {
    
    Product save(Product product);
    
    Optional<Product> findById(ProductId id);
    
    List<Product> findAll();
    
    List<Product> findByCategory(String categoryName);
    
    List<Product> findByBrand(String brand);
    
    List<Product> findByCategoryAndBrand(String categoryName, String brand);
    
    List<Product> findByName(String name);
    
    List<Product> findByBrandAndName(String brand, String name);
    
    boolean existsByNameAndBrand(String name, String brand);
    
    void deleteById(ProductId id);
}
