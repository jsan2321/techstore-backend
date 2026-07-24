package com.ecoapi.techstore.order.infrastructure.adapter.input.rest.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for placing a new order.
 * This is a pure data carrier - it represents exactly what the client sent.
 * 
 * The shipping address is optional:
 * - If provided: Uses the provided address for this order
 * - If not provided (null): Uses the user's saved profile address
 * 
 * This allows the frontend to:
 * 1. Quick checkout: Just send paymentMethod to use saved address
 * 2. Custom address: Send full shippingAddress for different delivery location
 * 
 * Note: If no address is provided AND user has no saved address, the order will fail.
 */
public record PlaceOrderRequest(
    /**
     * Optional shipping address for the order.
     * If null, the user's saved profile address will be used.
     * Required for shipping carrier integration and tax calculation.
     */
    @Valid
    ShippingAddressRequest shippingAddress,
    
    /**
     * Flag to explicitly use the user's profile address.
     * When true, ignores any provided shippingAddress and uses profile address.
     * Defaults to false if not provided.
     */
    Boolean useProfileAddress,
    
    /**
     * Optional delivery notes (can be provided even when using profile address).
     * Useful for gate codes, delivery instructions, etc.
     */
    String deliveryNotes,
    
    /**
     * Payment method identifier.
     * Must be a valid payment method (e.g., "CREDIT_CARD", "DEBIT_CARD", "PAYPAL").
     */
    @NotBlank(message = "Payment method is required")
    String paymentMethod
) {
    /**
     * Check if this request wants to use the user's profile address
     */
    public boolean shouldUseProfileAddress() {
        return Boolean.TRUE.equals(useProfileAddress) || shippingAddress == null;
    }
}
