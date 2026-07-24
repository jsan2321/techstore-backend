package com.ecoapi.techstore.product.infrastructure.adapter.output.persistence;

import com.ecoapi.techstore.product.application.port.out.CategoryRepositoryPort;
import com.ecoapi.techstore.product.domain.model.Category;
import com.ecoapi.techstore.product.domain.model.CategoryId;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.entity.CategoryEntity;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.mapper.ProductPersistenceMapper;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.repository.JpaCategoryRepository;

import java.util.Optional;

/**
 * Adapter implementing CategoryRepositoryPort
 * Bridges domain and JPA persistence
 */
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {
    
    private final JpaCategoryRepository jpaRepository;
    private final ProductPersistenceMapper mapper;
    
    public CategoryRepositoryAdapter(JpaCategoryRepository jpaRepository,
                                    ProductPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public Category save(Category category) {
        CategoryEntity entity = mapper.toCategoryEntity(category);
        CategoryEntity savedEntity = jpaRepository.save(entity);
        return mapper.toCategoryDomain(savedEntity);
    }
    
    @Override
    public Optional<Category> findById(CategoryId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toCategoryDomain);
    }
    
    @Override
    public Optional<Category> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(mapper::toCategoryDomain);
    }
    
    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }
}
