package com.ecoapi.techstore.order.application.port.out.dto;

/**
 * DTO for user address data retrieved from the User context.
 * This is an Anti-Corruption Layer (ACL) data structure that
 * protects the Order context from changes in the User context's model.
 */
public record UserAddressData(
    String recipientName,
    String street,
    String addressLine2,
    String city,
    String state,
    String zipCode,
    String country
) {
    /**
     * Checks if this address data has all required fields for shipping
     */
    public boolean isComplete() {
        return recipientName != null && !recipientName.isBlank()
                && street != null && !street.isBlank()
                && city != null && !city.isBlank()
                && state != null && !state.isBlank()
                && zipCode != null && !zipCode.isBlank()
                && country != null && !country.isBlank();
    }
}
