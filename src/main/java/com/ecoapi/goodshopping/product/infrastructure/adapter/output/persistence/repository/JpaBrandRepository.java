package com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.entity.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaBrandRepository extends JpaRepository<BrandEntity, Long> {
    
    Optional<BrandEntity> findByName(String name);
    
    boolean existsByName(String name);
}
