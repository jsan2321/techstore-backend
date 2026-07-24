package com.ecoapi.techstore.cart.infrastructure.adapter.input.rest.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddItemRequest(
    @NotNull(message = "Product ID is required")
    Long productId,

    @Min(value = 1, message = "Quantity must be at least 1")
    int quantity
) {}