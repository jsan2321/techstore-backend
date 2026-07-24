package com.ecoapi.techstore.product.application.service;

import com.ecoapi.techstore.product.application.port.in.GetBrandByIdUseCase;
import com.ecoapi.techstore.product.application.port.out.BrandRepositoryPort;
import com.ecoapi.techstore.product.domain.exception.BrandNotFoundException;
import com.ecoapi.techstore.product.domain.model.Brand;
import com.ecoapi.techstore.product.domain.model.BrandId;

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
