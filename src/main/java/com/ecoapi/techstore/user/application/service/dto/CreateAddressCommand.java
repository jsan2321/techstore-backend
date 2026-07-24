package com.ecoapi.techstore.user.application.service.dto;

import com.ecoapi.techstore.user.domain.valueobjects.AddressType;

/**
 * Command DTO for creating a saved address.
 */
public record CreateAddressCommand(
        Long userId,
        String label,
        String recipientName,
        String street,
        String addressLine2,
        String city,
        String state,
        String zipCode,
        String country,
        AddressType type,
        boolean isDefault
) {

    public CreateAddressCommand {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("Address label is required");
        }
        if (recipientName == null || recipientName.isBlank()) {
            throw new IllegalArgumentException("Recipient name is required");
        }
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street is required");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City is required");
        }
        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("State is required");
        }
        if (zipCode == null || zipCode.isBlank()) {
            throw new IllegalArgumentException("Zip code is required");
        }
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country is required");
        }
        if (type == null) {
            throw new IllegalArgumentException("Address type is required");
        }
    }
}
