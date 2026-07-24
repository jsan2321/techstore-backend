package com.ecoapi.techstore.product.domain.model;

import com.ecoapi.techstore.common.domain.valueobjects.Money;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;

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
    private int stock;
    private String description;
    private Category category;
    private ImageUrl imageUrl;
    
    // Discount fields
    private Integer discountPercentage;
    private boolean featured;
    private boolean active;
    
    // Private constructor - use factory methods
    private Product(String name, Brand brand, Money price, int stock,
                    String description, Category category) {
        validateName(name);
        validateBrand(brand);
        validatePrice(price);
        validateStock(stock);
        validateDescription(description);
        validateCategory(category);
        
        this.name = name.trim();
        this.brand = brand;
        this.price = price;
        this.stock = stock;
        this.description = description != null ? description.trim() : null;
        this.category = category;
        this.imageUrl = null;
        this.discountPercentage = null;
        this.featured = false;
        this.active = true;
    }
    
    // Private constructor for reconstitution - use factory methods
    private Product(ProductId id, String name, Brand brand, Money price, int stock,
                    String description, Category category, ImageUrl imageUrl, boolean active,
                    Integer discountPercentage, boolean featured) {
        validateName(name);
        validateBrand(brand);
        validatePrice(price);
        validateStock(stock);
        validateDescription(description);
        validateCategory(category);
        validateDiscountPercentage(discountPercentage);

        this.id = id;
        this.name = name.trim();
        this.brand = brand;
        this.price = price;
        this.stock = stock;
        this.description = description != null ? description.trim() : null;
        this.category = category;
        this.imageUrl = imageUrl;
        this.active = active;
        this.discountPercentage = discountPercentage;
        this.featured = featured;
    }
    
    // Factory Methods
    
    /**
     * Factory method for creating a new product
     * Used when adding products to the catalog
     */
    public static Product create(String name, Brand brand, Money price, int stock,
                                 String description, Category category) {
        return new Product(name, brand, price, stock, description, category);
    }
    
    /**
     * Factory method for reconstituting product from persistence
     * Used by infrastructure layer to rebuild domain object from database
     */
    public static Product reconstitute(ProductId id, String name, Brand brand, Money price,
                                       int stock, String description, Category category,
                                       ImageUrl imageUrl, boolean active) {
        return new Product(id, name, brand, price, stock, description, category, imageUrl, active, null, false);
    }

    public static Product reconstitute(ProductId id, String name, Brand brand, Money price,
                                       int stock, String description, Category category,
                                       ImageUrl imageUrl, boolean active,
                                       Integer discountPercentage, boolean featured) {
        return new Product(id, name, brand, price, stock, description, category, imageUrl, active,
                discountPercentage, featured);
    }
    
    // Business logic methods
    
    public void updateDetails(String name, Brand brand, Category category, Money price, String description) {
        validateName(name);
        validateBrand(brand);
        validateCategory(category);
        validatePrice(price);
        validateDescription(description);
        
        this.name = name.trim();
        this.brand = brand;
        this.category = category;
        this.price = price;
        this.description = description != null ? description.trim() : null;
    }

    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to add must be positive");
        }
        this.stock += quantity;
    }
    
    public void reduceStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to reduce must be positive");
        }
        if (quantity > this.stock) {
            throw new IllegalStateException("Insufficient stock. Available: " + this.stock);
        }
        this.stock -= quantity;
    }

    public boolean hasStock(int quantity) {
        return stock >= quantity;
    }

    public void applyDiscount(int percentage) {
        validateDiscountPercentage(percentage);
        this.discountPercentage = percentage;
    }

    public void removeDiscount() {
        this.discountPercentage = null;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }
    
    public void updateImageUrl(ImageUrl imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public void removeImage() {
        this.imageUrl = null;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
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
    
    private void validateStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Product stock cannot be negative");
        }
    }

    private void validateDiscountPercentage(Integer discountPercentage) {
        if (discountPercentage == null) {
            return;
        }
        if (discountPercentage < 1 || discountPercentage > 99) {
            throw new IllegalArgumentException("Product discount percentage must be between 1 and 99");
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
    
    public int getStock() {
        return stock;
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

    public Integer getDiscountPercentage() {
        return discountPercentage;
    }

    public boolean isFeatured() {
        return featured;
    }

    // equals and hashcode

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                '}';
    }
}
