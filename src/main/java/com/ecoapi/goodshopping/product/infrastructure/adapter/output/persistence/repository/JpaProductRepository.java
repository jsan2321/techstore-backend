package com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaProductRepository extends JpaRepository<ProductEntity, Long> {
    
    List<ProductEntity> findByCategoryName(String categoryName);
    
    List<ProductEntity> findByBrandName(String brandName);
    
    List<ProductEntity> findByCategoryNameAndBrandName(String categoryName, String brandName);
    
    List<ProductEntity> findByName(String name);
    
    List<ProductEntity> findByBrandNameAndName(String brandName, String name);
    
    boolean existsByNameAndBrandId(String name, Long brandId);
}
