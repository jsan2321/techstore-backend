package com.ecoapi.goodshopping.product.application.service;

import com.ecoapi.goodshopping.product.application.port.in.DeleteBrandUseCase;
import com.ecoapi.goodshopping.product.domain.exception.BrandNotFoundException;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.repository.JpaBrandRepository;

/**
 * Service for deleting brands
 */
public class DeleteBrandService implements DeleteBrandUseCase {
    
    private final JpaBrandRepository jpaBrandRepository;
    
    public DeleteBrandService(JpaBrandRepository jpaBrandRepository) {
        this.jpaBrandRepository = jpaBrandRepository;
    }
    
    @Override
    public void deleteBrand(Long id) {
        if (!jpaBrandRepository.existsById(id)) {
            throw new BrandNotFoundException("Brand not found with id: " + id);
        }
        jpaBrandRepository.deleteById(id);
    }
}
