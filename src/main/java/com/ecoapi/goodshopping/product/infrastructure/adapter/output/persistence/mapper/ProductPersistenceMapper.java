package com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.mapper;

import com.ecoapi.goodshopping.product.domain.model.*;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.entity.CategoryEntity;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.entity.ProductEntity;

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
        // Brand entity will be set by the calling service
        entity.setPrice(product.getPrice().value());
        entity.setInventory(product.getInventory());
        entity.setDescription(product.getDescription());
        entity.setImageUrl(product.getImageUrl() != null ? product.getImageUrl().value() : null);
        entity.setCategory(toCategoryEntity(product.getCategory()));
        
        return entity;
    }
    
    /**
     * Convert JPA ProductEntity to Domain Product
     */
    public Product toDomain(ProductEntity entity) {
        ProductId productId = entity.getId() != null ? ProductId.of(entity.getId()) : null;
        Category category = toCategoryDomain(entity.getCategory());
        Money price = Money.of(entity.getPrice());
        Brand brand = entity.getBrand() != null ? toBrandDomain(entity.getBrand()) : null;
        ImageUrl imageUrl = ImageUrl.ofNullable(entity.getImageUrl());
        
        return Product.reconstitute(
                productId,
                entity.getName(),
                brand,
                price,
                entity.getInventory(),
                entity.getDescription(),
                category,
                imageUrl
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
    public com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.entity.BrandEntity toBrandEntity(Brand brand) {
        com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.entity.BrandEntity entity = 
            new com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.entity.BrandEntity();
        
        if (brand.getId() != null) {
            entity.setId(brand.getId().value());
        }
        
        entity.setName(brand.getName());
        
        return entity;
    }
    
    /**
     * Convert JPA BrandEntity to Domain Brand
     */
    public Brand toBrandDomain(com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.entity.BrandEntity entity) {
        BrandId brandId = 
            entity.getId() != null ? BrandId.of(entity.getId()) : null;
        
        return new Brand(brandId, entity.getName());
    }
}
