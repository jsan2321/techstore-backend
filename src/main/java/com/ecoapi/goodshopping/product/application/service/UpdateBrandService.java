package com.ecoapi.goodshopping.product.application.service;

import com.ecoapi.goodshopping.product.application.port.in.UpdateBrandUseCase;
import com.ecoapi.goodshopping.product.application.port.out.BrandRepositoryPort;
import com.ecoapi.goodshopping.product.domain.exception.BrandNotFoundException;
import com.ecoapi.goodshopping.product.domain.model.Brand;
import com.ecoapi.goodshopping.product.domain.model.BrandId;

/**
 * Service for updating brands
 */
public class UpdateBrandService implements UpdateBrandUseCase {
    
    private final BrandRepositoryPort brandRepository;
    
    public UpdateBrandService(BrandRepositoryPort brandRepository) {
        this.brandRepository = brandRepository;
    }
    
    @Override
    public Brand updateBrand(Long id, String name) {
        Brand brand = brandRepository.findById(BrandId.of(id))
                .orElseThrow(() -> new BrandNotFoundException("Brand not found with id: " + id));
        
        // Check if new name already exists (and it's not the same brand)
        brandRepository.findByName(name).ifPresent(existingBrand -> {
            if (!existingBrand.getId().equals(brand.getId())) {
                throw new IllegalArgumentException("Brand with name '" + name + "' already exists");
            }
        });
        
        brand.changeName(name);
        return brandRepository.save(brand);
    }
}
