package com.ecoapi.techstore.user.domain.valueobjects;

/**
 * Value Object representing the unique identifier for a SavedAddress.
 */
public record AddressId(Long value) {

    public AddressId {
        if (value == null) {
            throw new IllegalArgumentException("AddressId value cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("AddressId must be positive");
        }
    }

    public static AddressId of(Long value) {
        return new AddressId(value);
    }
}
