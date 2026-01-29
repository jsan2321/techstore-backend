package com.ecoapi.goodshopping.product.application.service.dto;

import java.math.BigDecimal;

/**
 * Command DTO for adding a new product
 * Immutable data transfer object with validation
 */
public record ProductCommand(
    String name,
    Long brandId,
    BigDecimal price,
    int inventory,
    String description,
    Long categoryId
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
        if (inventory < 0) {
            throw new IllegalArgumentException("Product inventory cannot be negative");
        }
        if (description != null && description.length() > 1000) {
            throw new IllegalArgumentException("Product description cannot exceed 1000 characters");
        }
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID is required");
        }
    }
}
