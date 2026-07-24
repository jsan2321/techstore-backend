package com.ecoapi.techstore.product.application.service.dto;

import java.math.BigDecimal;

/**
 * Command DTO for adding a new product
 * Immutable data transfer object with validation
 */
public record ProductCommand(
    String name,
    Long brandId,
    BigDecimal price,
    int stock,
    String description,
    Long categoryId,
    boolean applyDiscount,
    Integer discountPercentage,
    boolean featured
) {
    public ProductCommand {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (name.length() > 200) {
            throw new IllegalArgumentException("Product name cannot exceed 200 characters");
        }
        if (brandId == null) {
            throw new IllegalArgumentException("Brand ID is required");
        }
        if (price == null) {
            throw new IllegalArgumentException("Product price is required");
        }
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Product stock cannot be negative");
        }
        if (description != null && description.length() > 1000) {
            throw new IllegalArgumentException("Product description cannot exceed 1000 characters");
        }
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID is required");
        }
        if (applyDiscount) {
            if (discountPercentage == null) {
                throw new IllegalArgumentException("Discount percentage is required when applyDiscount is true");
            }
            if (discountPercentage < 1 || discountPercentage > 99) {
                throw new IllegalArgumentException("Discount percentage must be between 1 and 99");
            }
        }
    }
}
