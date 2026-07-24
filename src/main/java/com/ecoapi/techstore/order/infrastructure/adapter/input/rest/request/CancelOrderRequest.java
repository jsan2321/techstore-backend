package com.ecoapi.techstore.order.infrastructure.adapter.input.rest.request;

import jakarta.validation.constraints.Size;

/**
 * Request DTO for cancelling an order.
 * This is a pure data carrier - it represents exactly what the client sent.
 * Default cancellation reasons are applied in the Application Layer, not here.
 */
public record CancelOrderRequest(
    /**
     * Reason for order cancellation.
     * Optional - the Application Layer will apply a default if null/blank.
     */
    @Size(max = 500, message = "Cancellation reason must not exceed 500 characters")
    String reason
) {}
