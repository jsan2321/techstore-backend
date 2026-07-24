package com.ecoapi.techstore.order.application.service.dto;

/**
 * Application layer DTO for structured shipping address.
 * Represents shipping address data passed from the controller to the use case.
 */
public record ShippingAddressData(
    String fullName,
    String street,
    String addressLine2,
    String city,
    String state,
    String postalCode,
    String country,
    String deliveryNotes
) {
    public ShippingAddressData {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street address is required");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City is required");
        }
        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("State/Province is required");
        }
        if (postalCode == null || postalCode.isBlank()) {
            throw new IllegalArgumentException("Postal code is required");
        }
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country is required");
        }
    }

    /**
     * Returns the full formatted address as a single string.
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
