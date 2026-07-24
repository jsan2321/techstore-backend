package com.ecoapi.techstore.order.domain.model;

import com.ecoapi.techstore.common.domain.valueobjects.Money;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;

import java.util.Objects;

public class OrderItem {
    
    private OrderItemId id;
    private ProductId productId;
    private String productName;
    private String productDescription;
    private String productImageUrl;
    private int quantity;
    private Money unitPrice;
    private Money totalPrice;
    
    public OrderItem(ProductId productId, String productName, String productDescription,
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
        this.totalPrice = unitPrice.multiply(quantity);
    }
    
    public OrderItem(OrderItemId id, ProductId productId, String productName, String productDescription,
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
    
    public OrderItemId getId() {
        return id;
    }
    
    public void setId(OrderItemId id) {
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
        OrderItem orderItem = (OrderItem) o;
        return id != null && Objects.equals(id, orderItem.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
