package com.ecoapi.techstore.product.application.service;

import com.ecoapi.techstore.product.application.port.in.UpdateCategoryUseCase;
import com.ecoapi.techstore.product.application.port.out.CategoryRepositoryPort;
import com.ecoapi.techstore.product.domain.exception.CategoryNotFoundException;
import com.ecoapi.techstore.product.domain.model.Category;
import com.ecoapi.techstore.product.domain.model.CategoryId;

/**
 * Service for updating categories
 */
public class UpdateCategoryService implements UpdateCategoryUseCase {
    
    private final CategoryRepositoryPort categoryRepository;
    
    public UpdateCategoryService(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    @Override
    public Category updateCategory(Long id, String name) {
        Category category = categoryRepository.findById(CategoryId.of(id))
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        
        // Check if new name already exists (and it's not the same category)
        categoryRepository.findByName(name).ifPresent(existingCategory -> {
            if (!existingCategory.getId().equals(category.getId())) {
                throw new IllegalArgumentException("Category with name '" + name + "' already exists");
            }
        });
        
        category.changeName(name);
        return categoryRepository.save(category);
    }
}
