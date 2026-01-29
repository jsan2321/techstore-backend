package com.ecoapi.goodshopping.product.application.service;

import com.ecoapi.goodshopping.product.application.port.in.GetCategoryByIdUseCase;
import com.ecoapi.goodshopping.product.application.port.out.CategoryRepositoryPort;
import com.ecoapi.goodshopping.product.domain.exception.CategoryNotFoundException;
import com.ecoapi.goodshopping.product.domain.model.Category;
import com.ecoapi.goodshopping.product.domain.model.CategoryId;

/**
 * Service for getting a category by ID
 */
public class GetCategoryByIdService implements GetCategoryByIdUseCase {
    
    private final CategoryRepositoryPort categoryRepository;
    
    public GetCategoryByIdService(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    @Override
    public Category execute(Long id) {
        return categoryRepository.findById(CategoryId.of(id))
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }
}
