package com.ecoapi.techstore.product.domain.model;

import java.util.Objects;

/**
 * Category Entity - Part of Product aggregate
 * Pure domain model with no infrastructure dependencies
 */
public class Category {
    
    private CategoryId id;
    private String name;
    
    // Constructor for creating new category
    public Category(String name) {
        validateName(name);
        this.name = name.trim();
    }
    
    // Constructor for reconstituting from persistence
    public Category(CategoryId id, String name) {
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
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Category name cannot exceed 100 characters");
        }
    }
    
    // Getters
    
    public CategoryId getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
