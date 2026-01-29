package com.ecoapi.goodshopping.product.application.service;

import com.ecoapi.goodshopping.product.application.port.in.AddBrandUseCase;
import com.ecoapi.goodshopping.product.application.port.out.BrandRepositoryPort;
import com.ecoapi.goodshopping.product.domain.model.Brand;

/**
 * Service for adding new brands
 */
public class AddBrandService implements AddBrandUseCase {
    
    private final BrandRepositoryPort brandRepository;
    
    public AddBrandService(BrandRepositoryPort brandRepository) {
        this.brandRepository = brandRepository;
    }
    
    @Override
    public Brand addBrand(String name) {
        // Check if brand already exists
        if (brandRepository.existsByName(name)) {
            throw new IllegalArgumentException("Brand with name '" + name + "' already exists");
        }
        
        // Create and save brand
        Brand brand = new Brand(name);
        return brandRepository.save(brand);
    }
}
