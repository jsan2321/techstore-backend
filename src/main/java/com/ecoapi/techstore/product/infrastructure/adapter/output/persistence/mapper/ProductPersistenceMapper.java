package com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.mapper;

import com.ecoapi.techstore.common.domain.valueobjects.Money;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;
import com.ecoapi.techstore.product.domain.model.*;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.entity.BrandEntity;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.entity.CategoryEntity;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.entity.ProductEntity;

/**
 * Mapper to convert between Domain Models and JPA Entities
 * Key component in Hexagonal Architecture for keeping domain pure
 */
public class ProductPersistenceMapper {
    
    /**
     * Convert Domain Product to JPA ProductEntity
     */
    public ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        
        if (product.getId() != null) {
            entity.setId(product.getId().value());
        }
        
        entity.setName(product.getName());
        entity.setBrand(toBrandEntity(product.getBrand()));
        entity.setPrice(product.getPrice().value());
        entity.setStock(product.getStock());
        entity.setDescription(product.getDescription());
        entity.setImageUrl(product.getImageUrl() != null ? product.getImageUrl().value() : null);
        entity.setCategory(toCategoryEntity(product.getCategory()));
        entity.setDiscountPercentage(product.getDiscountPercentage());
        entity.setFeatured(product.isFeatured());
        entity.setActive(product.isActive());

        return entity;
    }
    
    /**
     * Convert JPA ProductEntity to Domain Product
     */
    public Product toDomain(ProductEntity entity) {
        ProductId productId = entity.getId() != null ? ProductId.of(entity.getId()) : null;
        Category category = toCategoryDomain(entity.getCategory());
        Money price = Money.of(entity.getPrice());
        Brand brand = toBrandDomain(entity.getBrand());
        ImageUrl imageUrl = ImageUrl.ofNullable(entity.getImageUrl());
        
        return Product.reconstitute(
                productId,
                entity.getName(),
                brand,
                price,
            entity.getStock(),
                entity.getDescription(),
                category,
                imageUrl,
            entity.isActive(),
            entity.getDiscountPercentage(),
            entity.isFeatured()
        );
    }
    
    /**
     * Convert Domain Category to JPA CategoryEntity
     */
    public CategoryEntity toCategoryEntity(Category category) {
        CategoryEntity entity = new CategoryEntity();
        
        if (category.getId() != null) {
            entity.setId(category.getId().value());
        }
        
        entity.setName(category.getName());
        
        return entity;
    }
    
    /**
     * Convert JPA CategoryEntity to Domain Category
     */
    public Category toCategoryDomain(CategoryEntity entity) {
        CategoryId categoryId = entity.getId() != null ? CategoryId.of(entity.getId()) : null;
        
        return new Category(categoryId, entity.getName());
    }
    
    /**
     * Convert Domain Brand to JPA BrandEntity
     */
    public BrandEntity toBrandEntity(Brand brand) {
        BrandEntity entity = new BrandEntity();
        
        if (brand.getId() != null) {
            entity.setId(brand.getId().value());
        }
        
        entity.setName(brand.getName());
        
        return entity;
    }
    
    /**
     * Convert JPA BrandEntity to Domain Brand
     */
    public Brand toBrandDomain(BrandEntity entity) {
        BrandId brandId = entity.getId() != null ? BrandId.of(entity.getId()) : null;
        
        return new Brand(brandId, entity.getName());
    }
}
