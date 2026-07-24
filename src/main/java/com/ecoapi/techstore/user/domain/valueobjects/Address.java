package com.ecoapi.techstore.user.domain.valueobjects;

/**
 * Immutable Value Object representing an Address in the User context.
 *
 * This value object stores the core address information that users
 * can save in their address book for reuse across orders.
 */
public record Address(
    String street,
    String addressLine2,
    String city,
    String state,
    String zipCode,
    String country
) {
    public Address {
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street is required");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City is required");
        }
        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("State/Province is required");
        }
        if (zipCode == null || zipCode.isBlank()) {
            throw new IllegalArgumentException("Zip code is required");
        }
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country is required");
        }

        // Normalize fields
        street = street.trim();
        addressLine2 = addressLine2 != null ? addressLine2.trim() : null;
        city = city.trim();
        state = state.trim();
        zipCode = zipCode.trim();
        country = country.trim().toUpperCase();
    }

    /**
     * Factory method to create from primitives
     */
    public static Address of(String street, String addressLine2, String city,
                             String state, String zipCode, String country) {
        return new Address(street, addressLine2, city, state, zipCode, country);
    }

    /**
     * Returns the full formatted address as a single string for display purposes.
     */
    public String toFormattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(street);
        if (addressLine2 != null && !addressLine2.isBlank()) {
            sb.append(", ").append(addressLine2);
        }
        sb.append("\n").append(city).append(", ").append(state).append(" ").append(zipCode);
        sb.append("\n").append(country);
        return sb.toString();
    }

    /**
     * Returns single-line formatted address
     */
    public String toSingleLineString() {
        StringBuilder sb = new StringBuilder();
        sb.append(street);
        if (addressLine2 != null && !addressLine2.isBlank()) {
            sb.append(", ").append(addressLine2);
        }
        sb.append(", ").append(city).append(", ").append(state).append(" ").append(zipCode);
        sb.append(", ").append(country);
        return sb.toString();
    }
}
