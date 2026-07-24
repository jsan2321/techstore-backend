package com.ecoapi.techstore.user.infrastructure.adapter.input.rest.request;

import com.ecoapi.techstore.user.domain.valueobjects.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * REST request DTO for updating a saved address.
 */
public record UpdateAddressRequest(
        @NotBlank(message = "Address label is required")
        @Size(max = 50, message = "Address label must not exceed 50 characters")
        String label,

        @NotBlank(message = "Recipient name is required")
        @Size(max = 100, message = "Recipient name must not exceed 100 characters")
        String recipientName,

        @NotBlank(message = "Street is required")
        @Size(max = 255, message = "Street must not exceed 255 characters")
        String street,

        @Size(max = 255, message = "Address line 2 must not exceed 255 characters")
        String addressLine2,

        @NotBlank(message = "City is required")
        @Size(max = 255, message = "City must not exceed 255 characters")
        String city,

        @NotBlank(message = "State is required")
        @Size(max = 255, message = "State must not exceed 255 characters")
        String state,

        @NotBlank(message = "Zip code is required")
        @Size(max = 255, message = "Zip code must not exceed 255 characters")
        String zipCode,

        @NotBlank(message = "Country is required")
        @Size(min = 2, max = 2, message = "Country must be a 2-letter ISO code")
        String country,

        @NotNull(message = "Address type is required")
        AddressType type
) {
}
