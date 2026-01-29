package com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for Category operations
 */
public record CategoryRequest(
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must contain 2-100 characters")
    String name
) {}
