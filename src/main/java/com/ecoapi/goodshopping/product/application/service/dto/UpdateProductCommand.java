package com.ecoapi.goodshopping.product.application.service.dto;

import java.math.BigDecimal;

/**
 * Command DTO for updating an existing product
 * Immutable data transfer object with validation
 */
public record UpdateProductCommand(
    String name,
    String brand,
    BigDecimal price,
    int inventory,
    String description,
    String categoryName
) {
    public UpdateProductCommand {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (name.length() > 200) {
            throw new IllegalArgumentException("Product name cannot exceed 200 characters");
        }
        if (brand == null || brand.isBlank()) {
            throw new IllegalArgumentException("Product brand is required");
        }
        if (brand.length() > 100) {
            throw new IllegalArgumentException("Product brand cannot exceed 100 characters");
        }
        if (price == null) {
            throw new IllegalArgumentException("Product price is required");
        }
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }
        if (inventory < 0) {
            throw new IllegalArgumentException("Product inventory cannot be negative");
        }
        if (description != null && description.length() > 1000) {
            throw new IllegalArgumentException("Product description cannot exceed 1000 characters");
        }
        if (categoryName == null || categoryName.isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }
    }
}
