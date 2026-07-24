package com.ecoapi.techstore.product.application.port.out;

import com.ecoapi.techstore.product.domain.model.Category;
import com.ecoapi.techstore.product.domain.model.CategoryId;

import java.util.Optional;

/**
 * Output Port for Category repository operations
 */
public interface CategoryRepositoryPort {
    
    Category save(Category category);
    
    Optional<Category> findById(CategoryId id);
    
    Optional<Category> findByName(String name);
    
    boolean existsByName(String name);
}
