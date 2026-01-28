package com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence;

import com.ecoapi.goodshopping.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.goodshopping.product.domain.model.Product;
import com.ecoapi.goodshopping.product.domain.model.ProductId;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.entity.ProductEntity;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.mapper.ProductPersistenceMapper;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.repository.JpaProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementing ProductRepositoryPort
 * Bridges domain and JPA persistence
 */
public class ProductRepositoryAdapter implements ProductRepositoryPort {
    
    private final JpaProductRepository jpaRepository;
    private final ProductPersistenceMapper mapper;
    
    public ProductRepositoryAdapter(JpaProductRepository jpaRepository,
                                   ProductPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public Product save(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        ProductEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Product> findById(ProductId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByCategory(String categoryName) {
        return jpaRepository.findByCategoryName(categoryName).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByBrand(String brand) {
        return jpaRepository.findByBrand(brand).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByCategoryAndBrand(String categoryName, String brand) {
        return jpaRepository.findByCategoryNameAndBrand(categoryName, brand).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByName(String name) {
        return jpaRepository.findByName(name).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByBrandAndName(String brand, String name) {
        return jpaRepository.findByBrandAndName(brand, name).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByNameAndBrand(String name, String brand) {
        return jpaRepository.existsByNameAndBrand(name, brand);
    }
    
    @Override
    public void deleteById(ProductId id) {
        jpaRepository.deleteById(id.value());
    }
}
