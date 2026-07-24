package com.ecoapi.techstore.cart.domain.model;

import com.ecoapi.techstore.common.domain.valueobjects.Money;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;

import java.util.Objects;

/**
 * CartItem Entity - Represents an item in the shopping cart
 * Contains business logic for quantity and price calculations
 */
public class CartItem {
    
    private CartItemId id;
    private ProductId productId;
    private String productName;
    private String productDescription;
    private String productImageUrl;
    private int quantity;
    private Money unitPrice;
    private Money totalPrice;
    
    // Constructor for creating new cart item
    public CartItem(ProductId productId, String productName, String productDescription,
                    String productImageUrl,
                    int quantity, Money unitPrice) {
        validateProductId(productId);
        validateProductName(productName);
        validateProductDescription(productDescription);
        validateProductImageUrl(productImageUrl);
        validateQuantity(quantity);
        validateUnitPrice(unitPrice);
        
        this.productId = productId;
        this.productName = productName;
        this.productDescription = normalizeDescription(productDescription);
        this.productImageUrl = normalizeImageUrl(productImageUrl);
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        //this.totalPrice = unitPrice.multiply(quantity);
        recalculateTotalPrice();
    }
    
    // Constructor for reconstituting from persistence
    public CartItem(CartItemId id, ProductId productId, String productName, String productDescription,
                   String productImageUrl,
                   int quantity, Money unitPrice, Money totalPrice) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        validateProductDescription(productDescription);
        validateProductImageUrl(productImageUrl);
        this.productDescription = normalizeDescription(productDescription);
        this.productImageUrl = normalizeImageUrl(productImageUrl);
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }
    
    // Business logic methods
    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.quantity += amount;
        recalculateTotalPrice();
    }
    
    public void updateQuantity(int newQuantity) {
        validateQuantity(newQuantity);
        this.quantity = newQuantity;
        recalculateTotalPrice();
    }
    
    public void updateUnitPrice(Money newUnitPrice) {
        validateUnitPrice(newUnitPrice);
        this.unitPrice = newUnitPrice;
        recalculateTotalPrice();
    }
    
    private void recalculateTotalPrice() {
        this.totalPrice = this.unitPrice.multiply(this.quantity);
        //this.totalPrice = this.unitPrice.multiply(this.quantity + 1);
    }
    
    public boolean isForProduct(ProductId productId) {
        return this.productId.equals(productId);
    }
    
    // Validations
    private void validateProductId(ProductId productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
    }
    
    private void validateProductName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
    }

    private void validateProductDescription(String productDescription) {
        if (productDescription != null && productDescription.length() > 1000) {
            throw new IllegalArgumentException("Product description cannot exceed 1000 characters");
        }
    }

    private void validateProductImageUrl(String productImageUrl) {
        if (productImageUrl != null && productImageUrl.length() > 2048) {
            throw new IllegalArgumentException("Product image URL cannot exceed 2048 characters");
        }
    }

    private String normalizeDescription(String productDescription) {
        if (productDescription == null) {
            return null;
        }
        String trimmed = productDescription.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeImageUrl(String productImageUrl) {
        if (productImageUrl == null) {
            return null;
        }
        String trimmed = productImageUrl.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
    
    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }
    
    private void validateUnitPrice(Money unitPrice) {
        if (unitPrice == null) {
            throw new IllegalArgumentException("Unit price cannot be null");
        }
    }
    
    // Getters
    public CartItemId getId() {
        return id;
    }
    
    public void setId(CartItemId id) {
        this.id = id;
    }
    
    public ProductId getProductId() {
        return productId;
    }
    
    public String getProductName() {
        return productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public Money getUnitPrice() {
        return unitPrice;
    }
    
    public Money getTotalPrice() {
        return totalPrice;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return id != null && Objects.equals(id, cartItem.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
