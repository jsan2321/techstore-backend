package com.ecoapi.goodshopping.product.domain.model;

import java.util.Objects;

/**
 * Product Aggregate Root - Pure domain model
 * Contains business logic and invariants
 * No JPA annotations - this is the core business entity
 */
public class Product {
    
    private ProductId id;
    private String name;
    private Brand brand;
    private Money price;
    private int inventory;
    private String description;
    private Category category;
    private ImageUrl imageUrl;
    
    // Private constructor - use factory methods
    private Product(String name, Brand brand, Money price, int inventory, 
                    String description, Category category) {
        validateName(name);
        validateBrand(brand);
        validatePrice(price);
        validateInventory(inventory);
        validateDescription(description);
        validateCategory(category);
        
        this.name = name.trim();
        this.brand = brand;
        this.price = price;
        this.inventory = inventory;
        this.description = description != null ? description.trim() : null;
        this.category = category;
        this.imageUrl = null;
    }
    
    // Private constructor for reconstitution - use factory methods
    private Product(ProductId id, String name, Brand brand, Money price, int inventory,
                    String description, Category category, ImageUrl imageUrl) {
        validateName(name);
        validateBrand(brand);
        validatePrice(price);
        validateInventory(inventory);
        validateDescription(description);
        validateCategory(category);
        
        this.id = id;
        this.name = name.trim();
        this.brand = brand;
        this.price = price;
        this.inventory = inventory;
        this.description = description != null ? description.trim() : null;
        this.category = category;
        this.imageUrl = imageUrl;
    }
    
    // Factory Methods
    
    /**
     * Factory method for creating a new product
     * Used when adding products to the catalog
     */
    public static Product create(String name, Brand brand, Money price, int inventory,
                                 String description, Category category) {
        return new Product(name, brand, price, inventory, description, category);
    }
    
    /**
     * Factory method for reconstituting product from persistence
     * Used by infrastructure layer to rebuild domain object from database
     */
    public static Product reconstitute(ProductId id, String name, Brand brand, Money price,
                                       int inventory, String description, Category category, ImageUrl imageUrl) {
        return new Product(id, name, brand, price, inventory, description, category, imageUrl);
    }
    
    // Business logic methods
    
    public void updateDetails(String name, Brand brand, Money price, String description) {
        validateName(name);
        validateBrand(brand);
        validatePrice(price);
        validateDescription(description);
        
        this.name = name.trim();
        this.brand = brand;
        this.price = price;
        this.description = description != null ? description.trim() : null;
    }
    
    public void changeCategory(Category newCategory) {
        validateCategory(newCategory);
        this.category = newCategory;
    }
    
    public void updatePrice(Money newPrice) {
        validatePrice(newPrice);
        this.price = newPrice;
    }
    
    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to add must be positive");
        }
        this.inventory += quantity;
    }
    
    public void reduceStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to reduce must be positive");
        }
        if (quantity > this.inventory) {
            throw new IllegalStateException("Insufficient inventory. Available: " + this.inventory);
        }
        this.inventory -= quantity;
    }
    
    public boolean isInStock() {
        return inventory > 0;
    }
    
    public boolean hasStock(int quantity) {
        return inventory >= quantity;
    }
    
    public void updateImageUrl(ImageUrl imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public void removeImage() {
        this.imageUrl = null;
    }
    
    // Validation methods
    
    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (name.length() > 200) {
            throw new IllegalArgumentException("Product name cannot exceed 200 characters");
        }
    }
    
    private void validateBrand(Brand brand) {
        if (brand == null) {
            throw new IllegalArgumentException("Product brand cannot be null");
        }
    }
    
    private void validatePrice(Money price) {
        if (price == null) {
            throw new IllegalArgumentException("Product price cannot be null");
        }
    }
    
    private void validateInventory(int inventory) {
        if (inventory < 0) {
            throw new IllegalArgumentException("Product inventory cannot be negative");
        }
    }
    
    private void validateDescription(String description) {
        if (description != null && description.length() > 1000) {
            throw new IllegalArgumentException("Product description cannot exceed 1000 characters");
        }
    }
    
    private void validateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Product category cannot be null");
        }
    }
    
    // Getters
    
    public ProductId getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public Brand getBrand() {
        return brand;
    }
    
    public Money getPrice() {
        return price;
    }
    
    public int getInventory() {
        return inventory;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public ImageUrl getImageUrl() {
        return imageUrl;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(name, product.name) && Objects.equals(brand, product.brand);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, brand);
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", price=" + price +
                ", inventory=" + inventory +
                '}';
    }
}
