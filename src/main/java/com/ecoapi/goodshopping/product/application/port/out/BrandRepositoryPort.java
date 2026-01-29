package com.ecoapi.goodshopping.product.application.port.out;

import com.ecoapi.goodshopping.product.domain.model.Brand;
import com.ecoapi.goodshopping.product.domain.model.BrandId;

import java.util.Optional;

/**
 * Output Port for Brand repository operations
 */
public interface BrandRepositoryPort {
    
    Brand save(Brand brand);
    
    Optional<Brand> findById(BrandId id);
    
    Optional<Brand> findByName(String name);
    
    boolean existsByName(String name);
}
