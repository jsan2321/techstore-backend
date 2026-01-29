package com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for Brand operations
 */
public record BrandRequest(
    @NotBlank(message = "Brand name is required")
    @Size(min = 2, max = 100, message = "Brand name must contain 2-100 characters")
    String name
) {}
