package com.ecoapi.techstore.cart.infrastructure.adapter.input.rest.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for updating cart item quantity
 */
public record UpdateQuantityRequest(
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity
) {}