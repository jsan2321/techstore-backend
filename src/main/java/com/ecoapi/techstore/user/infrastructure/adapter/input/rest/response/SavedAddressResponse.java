package com.ecoapi.techstore.user.infrastructure.adapter.input.rest.response;

import com.ecoapi.techstore.user.domain.model.SavedAddress;

/**
 * REST response DTO for a user's saved address.
 */
public record SavedAddressResponse(
        Long id,
        String label,
        String recipientName,
        String street,
        String addressLine2,
        String city,
        String state,
        String zipCode,
        String country,
        String type,
        boolean isDefault
) {

    public static SavedAddressResponse from(SavedAddress savedAddress) {
        return new SavedAddressResponse(
                savedAddress.getId() != null ? savedAddress.getId().value() : null,
                savedAddress.getLabel(),
                savedAddress.getRecipientName(),
                savedAddress.getAddress().street(),
                savedAddress.getAddress().addressLine2(),
                savedAddress.getAddress().city(),
                savedAddress.getAddress().state(),
                savedAddress.getAddress().zipCode(),
                savedAddress.getAddress().country(),
                savedAddress.getType().name(),
                savedAddress.isDefault()
        );
    }
}
