package com.ecoapi.goodshopping.product.domain.model;

import java.util.Objects;

/**
 * Value Object representing a Product Brand
 * Ensures brand name is valid and properly formatted
 */
public class Brand {
    
    private final String name;
    
    private Brand(String name) {
        this.name = name.trim();
    }
    
    public static Brand of(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Brand name cannot be null or empty");
        }
        
        String trimmed = name.trim();
        
        if (trimmed.length() > 100) {
            throw new IllegalArgumentException("Brand name cannot exceed 100 characters");
        }
        
        return new Brand(trimmed);
    }
    
    public String value() {
        return name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Brand brand = (Brand) o;
        return Objects.equals(name, brand.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}
