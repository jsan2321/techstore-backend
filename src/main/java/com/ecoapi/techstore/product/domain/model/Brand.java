package com.ecoapi.techstore.product.domain.model;

import java.util.Objects;

/**
 * Brand Entity - Part of Product aggregate
 * Pure domain model with no infrastructure dependencies
 * Refactored to match Category structure
 */
public class Brand {
    
    private BrandId id;
    private String name;
    
    // Constructor for creating new brand
    public Brand(String name) {
        validateName(name);
        this.name = name.trim();
    }
    
    // Constructor for reconstituting from persistence
    public Brand(BrandId id, String name) {
        validateName(name);
        this.id = id;
        this.name = name.trim();
    }
    
    // Business logic
    
    public void changeName(String newName) {
        validateName(newName);
        this.name = newName.trim();
    }
    
    // Validation
    
    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Brand name cannot be empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Brand name cannot exceed 100 characters");
        }
    }
    
    // Getters
    
    public BrandId getId() {
        return id;
    }
    
    public String getName() {
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
        return "Brand{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
