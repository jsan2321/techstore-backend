package com.ecoapi.techstore.product.application.service;

import com.ecoapi.techstore.product.application.port.in.DeleteCategoryUseCase;
import com.ecoapi.techstore.product.domain.exception.CategoryNotFoundException;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.repository.JpaCategoryRepository;

/**
 * Service for deleting categories
 */
public class DeleteCategoryService implements DeleteCategoryUseCase {
    
    private final JpaCategoryRepository jpaCategoryRepository;
    
    public DeleteCategoryService(JpaCategoryRepository jpaCategoryRepository) {
        this.jpaCategoryRepository = jpaCategoryRepository;
    }
    
    @Override
    public void deleteCategory(Long id) {
        if (!jpaCategoryRepository.existsById(id)) {
            throw new CategoryNotFoundException("Category not found with id: " + id);
        }
        jpaCategoryRepository.deleteById(id);
    }
}
