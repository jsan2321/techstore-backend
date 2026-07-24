package com.ecoapi.techstore.product.application.service;

import com.ecoapi.techstore.product.application.port.in.AddCategoryUseCase;
import com.ecoapi.techstore.product.application.port.out.CategoryRepositoryPort;
import com.ecoapi.techstore.product.domain.model.Category;

/**
 * Service for adding new categories
 */
public class AddCategoryService implements AddCategoryUseCase {
    
    private final CategoryRepositoryPort categoryRepository;
    
    public AddCategoryService(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    @Override
    public Category addCategory(String name) {
        // Check if category already exists
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Category with name '" + name + "' already exists");
        }
        
        // Create and save category
        Category category = new Category(name);
        return categoryRepository.save(category);
    }
}
