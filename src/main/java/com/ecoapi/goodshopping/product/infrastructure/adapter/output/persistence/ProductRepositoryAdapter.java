package com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence;

import com.ecoapi.goodshopping.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.goodshopping.product.application.service.dto.ProductReadModel;
import com.ecoapi.goodshopping.product.domain.model.Product;
import com.ecoapi.goodshopping.product.domain.model.ProductId;
import com.ecoapi.goodshopping.product.domain.model.ProductSearchCriteria;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.entity.BrandEntity;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.entity.ProductEntity;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.mapper.ProductPersistenceMapper;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.repository.JpaBrandRepository;
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
    private final JpaBrandRepository jpaBrandRepository;
    private final ProductPersistenceMapper mapper;
    
    public ProductRepositoryAdapter(JpaProductRepository jpaRepository,
                                   JpaBrandRepository jpaBrandRepository,
                                   ProductPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.jpaBrandRepository = jpaBrandRepository;
        this.mapper = mapper;
    }
    
    @Override
    public Product save(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        
        // Find or create the brand entity
        String brandName = product.getBrand().getName();
        BrandEntity brandEntity = jpaBrandRepository.findByName(brandName)
                .orElseGet(() -> {
                    BrandEntity newBrand = new BrandEntity();
                    newBrand.setName(brandName);
                    return jpaBrandRepository.save(newBrand);
                });
        
        entity.setBrand(brandEntity);
        
        ProductEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public void deleteById(ProductId id) {
        jpaRepository.deleteById(id.value());
    }
    
    @Override
    public Optional<Product> findById(ProductId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsByNameAndBrandId(String name, Long brandId) {
        return jpaRepository.existsByNameAndBrandId(name, brandId);
    }
    
    // CQRS: Read Model Operations
    
    @Override
    public List<ProductReadModel> findAllAsReadModel() {
        return jpaRepository.findAll().stream()
                .map(this::toReadModel)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductReadModel> searchByCriteria(ProductSearchCriteria criteria) {
        // Start with all products
        List<ProductEntity> results = jpaRepository.findAll();
        
        // Apply filters
        if (criteria.category() != null && !criteria.category().isBlank()) {
            results = results.stream()
                    .filter(p -> p.getCategory() != null && 
                            p.getCategory().getName().equalsIgnoreCase(criteria.category()))
                    .collect(Collectors.toList());
        }
        
        if (criteria.brand() != null && !criteria.brand().isBlank()) {
            results = results.stream()
                    .filter(p -> p.getBrand() != null && 
                            p.getBrand().getName().equalsIgnoreCase(criteria.brand()))
                    .collect(Collectors.toList());
        }
        
        if (criteria.minPrice() != null) {
            results = results.stream()
                    .filter(p -> p.getPrice().compareTo(criteria.minPrice()) >= 0)
                    .collect(Collectors.toList());
        }
        
        if (criteria.maxPrice() != null) {
            results = results.stream()
                    .filter(p -> p.getPrice().compareTo(criteria.maxPrice()) <= 0)
                    .collect(Collectors.toList());
        }
        
        if (criteria.inStock() != null) {
            if (criteria.inStock()) {
                results = results.stream()
                        .filter(p -> p.getInventory() > 0)
                        .collect(Collectors.toList());
            } else {
                results = results.stream()
                        .filter(p -> p.getInventory() == 0)
                        .collect(Collectors.toList());
            }
        }
        
        return results.stream()
                .map(this::toReadModel)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<ProductReadModel> findByIdAsReadModel(Long id) {
        return jpaRepository.findById(id)
                .map(this::toReadModel);
    }
    
    private ProductReadModel toReadModel(ProductEntity entity) {
        return new ProductReadModel(
                entity.getId(),
                entity.getName(),
                entity.getBrand() != null ? entity.getBrand().getName() : null,
                entity.getPrice(),
                entity.getInventory(),
                entity.getDescription(),
                entity.getCategory() != null ? entity.getCategory().getName() : null,
                entity.getImageUrl()
        );
    }
    
    // Deprecated: Legacy methods for backward compatibility
    
    @Override
    @Deprecated
    public List<Product> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Deprecated
    public List<Product> findByCategory(String categoryName) {
        return jpaRepository.findByCategoryName(categoryName).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Deprecated
    public List<Product> findByBrand(String brand) {
        return jpaRepository.findByBrandName(brand).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Deprecated
    public List<Product> findByCategoryAndBrand(String categoryName, String brand) {
        return jpaRepository.findByCategoryNameAndBrandName(categoryName, brand).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Deprecated
    public List<Product> findByName(String name) {
        return jpaRepository.findByName(name).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Deprecated
    public List<Product> findByBrandAndName(String brand, String name) {
        return jpaRepository.findByBrandNameAndName(brand, name).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
