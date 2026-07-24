package com.ecoapi.techstore.order.domain.valueobjects;

/**
 * Immutable Value Object representing a shipping address for an Order.
 *
 * This is an Order-specific value object that captures all the details
 * needed for shipping at the time of order placement. It has no independent
 * identity and is persisted embedded in the order table.
 *
 * Orders represent legal contracts, so the address must be immutable
 * and captured at the point of sale - regardless of whether the address
 * came from the user's saved address book or was entered manually.
 */
public record ShippingAddress(
    String fullName,
    String street,
    String addressLine2,
    String city,
    String state,
    String postalCode,
    String country,
    String deliveryNotes
) {
    public ShippingAddress {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full Name is required");
        }
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street is required");
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

        // Normalize fields
        fullName = fullName.trim();
        street = street.trim();
        addressLine2 = addressLine2 != null ? addressLine2.trim() : null;
        city = city.trim();
        state = state.trim();
        postalCode = postalCode.trim();
        country = country.trim().toUpperCase();
        deliveryNotes = deliveryNotes != null ? deliveryNotes.trim() : null;
    }

    /**
     * Factory method to create from primitives
     */
    public static ShippingAddress of(String fullName, String street, String addressLine2,
                                     String city, String state, String postalCode,
                                     String country, String deliveryNotes) {
        return new ShippingAddress(fullName, street, addressLine2, city, state,
                                   postalCode, country, deliveryNotes);
    }

    /**
     * Returns the full formatted address as a single string for display purposes.
     */
    public String toFormattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(fullName).append("\n");
        sb.append(street);
        if (addressLine2 != null && !addressLine2.isBlank()) {
            sb.append(", ").append(addressLine2);
        }
        sb.append("\n").append(city).append(", ").append(state).append(" ").append(postalCode);
        sb.append("\n").append(country);
        if (deliveryNotes != null && !deliveryNotes.isBlank()) {
            sb.append("\nNotes: ").append(deliveryNotes);
        }
        return sb.toString();
    }

    /**
     * Returns single-line formatted address
     */
    public String toSingleLineString() {
        StringBuilder sb = new StringBuilder();
        sb.append(fullName).append(" - ");
        sb.append(street);
        if (addressLine2 != null && !addressLine2.isBlank()) {
            sb.append(", ").append(addressLine2);
        }
        sb.append(", ").append(city).append(", ").append(state).append(" ").append(postalCode);
        sb.append(", ").append(country);
        return sb.toString();
    }
}
