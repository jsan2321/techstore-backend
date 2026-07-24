package com.ecoapi.techstore.product.infrastructure.adapter.output.persistence;

import com.ecoapi.techstore.product.application.port.out.BrandRepositoryPort;
import com.ecoapi.techstore.product.domain.model.Brand;
import com.ecoapi.techstore.product.domain.model.BrandId;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.mapper.ProductPersistenceMapper;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.repository.JpaBrandRepository;

import java.util.Optional;

/**
 * Adapter implementing BrandRepositoryPort
 * Bridges domain and JPA persistence
 */
public class BrandRepositoryAdapter implements BrandRepositoryPort {
    
    private final JpaBrandRepository jpaRepository;
    private final ProductPersistenceMapper mapper;
    
    public BrandRepositoryAdapter(JpaBrandRepository jpaRepository,
                                  ProductPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public Brand save(Brand brand) {
        com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.entity.BrandEntity entity = 
            mapper.toBrandEntity(brand);
        com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.entity.BrandEntity savedEntity = 
            jpaRepository.save(entity);
        return mapper.toBrandDomain(savedEntity);
    }
    
    @Override
    public Optional<Brand> findById(BrandId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toBrandDomain);
    }
    
    @Override
    public Optional<Brand> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(mapper::toBrandDomain);
    }
    
    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }
}
