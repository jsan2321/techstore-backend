package com.ecoapi.goodshopping.product.domain.service;

import com.ecoapi.goodshopping.product.domain.model.Product;

import java.math.BigDecimal;

/**
 * Domain Service for Product-related operations that don't belong to a single entity
 * Pure domain logic, no infrastructure concerns
 */
public class ProductDomainService {
    
    /**
     * Determines if a product should be marked as "low stock"
     * Business rule: Products with less than 10 units are considered low stock
     */
    public boolean isLowStock(Product product) {
        return product.getInventory() < 10;
    }
    
    /**
     * Determines if a product is out of stock
     */
    public boolean isOutOfStock(Product product) {
        return !product.isInStock();
    }
    
    /**
     * Calculates discount price
     * Business rule: Apply percentage discount to current price
     */
    public BigDecimal calculateDiscountPrice(Product product, int discountPercentage) {
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        
        BigDecimal discountMultiplier = BigDecimal.valueOf(100 - discountPercentage)
                .divide(BigDecimal.valueOf(100));
        return product.getPrice().value().multiply(discountMultiplier);
    }
    
    /**
     * Checks if two products are from the same category
     */
    public boolean isSameCategory(Product product1, Product product2) {
        return product1.getCategory().getId().equals(product2.getCategory().getId());
    }
    
    /**
     * Validates if a product can be deleted
     * Business rule: Products that are in active orders cannot be deleted
     */
    public boolean canDeleteProduct(Product product) {
        // In future: check if product is in any active orders
        // For now, only check if it's in stock
        return product.getInventory() == 0;
    }
}
