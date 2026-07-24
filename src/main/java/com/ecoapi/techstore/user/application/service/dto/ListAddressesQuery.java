package com.ecoapi.techstore.user.application.service.dto;

/**
 * Query DTO for listing saved addresses of the current user.
 */
public record ListAddressesQuery(Long userId) {

    public ListAddressesQuery {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
    }
}
