package com.ecoapi.goodshopping.product.application.service;

import com.ecoapi.goodshopping.product.application.port.in.GetBrandByIdUseCase;
import com.ecoapi.goodshopping.product.application.port.out.BrandRepositoryPort;
import com.ecoapi.goodshopping.product.domain.exception.BrandNotFoundException;
import com.ecoapi.goodshopping.product.domain.model.Brand;
import com.ecoapi.goodshopping.product.domain.model.BrandId;

/**
 * Service for getting a brand by ID
 */
public class GetBrandByIdService implements GetBrandByIdUseCase {
    
    private final BrandRepositoryPort brandRepository;
    
    public GetBrandByIdService(BrandRepositoryPort brandRepository) {
        this.brandRepository = brandRepository;
    }
    
    @Override
    public Brand execute(Long id) {
        return brandRepository.findById(BrandId.of(id))
                .orElseThrow(() -> new BrandNotFoundException("Brand not found with id: " + id));
    }
}
