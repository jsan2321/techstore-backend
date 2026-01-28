package com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaProductRepository extends JpaRepository<ProductEntity, Long> {
    
    List<ProductEntity> findByCategoryName(String categoryName);
    
    List<ProductEntity> findByBrand(String brand);
    
    List<ProductEntity> findByCategoryNameAndBrand(String categoryName, String brand);
    
    List<ProductEntity> findByName(String name);
    
    List<ProductEntity> findByBrandAndName(String brand, String name);
    
    boolean existsByNameAndBrand(String name, String brand);
}
