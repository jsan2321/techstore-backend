package com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, Long> {
    
    Optional<CategoryEntity> findByName(String name);
    
    boolean existsByName(String name);
}
