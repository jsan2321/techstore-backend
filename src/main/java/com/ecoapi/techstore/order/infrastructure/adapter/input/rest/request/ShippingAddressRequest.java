package com.ecoapi.techstore.order.infrastructure.adapter.input.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Structured shipping address for order placement.
 * Required for integration with shipping carriers (FedEx, DHL) and tax calculation APIs.
 */
public record ShippingAddressRequest(
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    String fullName,

    @NotBlank(message = "Street address is required")
    @Size(max = 255, message = "Street address must not exceed 255 characters")
    String street,
    
    @Size(max = 100, message = "Address line 2 must not exceed 100 characters")
    String addressLine2,
    
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    String city,
    
    @NotBlank(message = "State/Province is required")
    @Size(max = 100, message = "State/Province must not exceed 100 characters")
    String state,
    
    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    String postalCode,
    
    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 2, message = "Country must be a 2-letter ISO code")
    String country,
    
    @Size(max = 500, message = "Delivery notes must not exceed 500 characters")
    String deliveryNotes
) {
    /**
     * Returns the full formatted address as a single string.
     * Useful for display purposes.
     */
    public String toFormattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(fullName).append("\n");
        sb.append(street);
        if (addressLine2 != null && !addressLine2.isBlank()) {
            sb.append(", ").append(addressLine2);
        }
        sb.append(", ").append(city).append(", ").append(state).append(" ").append(postalCode);
        sb.append(", ").append(country);
        return sb.toString();
    }
}
