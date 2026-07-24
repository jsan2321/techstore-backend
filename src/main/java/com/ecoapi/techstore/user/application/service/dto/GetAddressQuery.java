package com.ecoapi.techstore.user.application.service.dto;

/**
 * Query DTO for retrieving one saved address by id.
 */
public record GetAddressQuery(Long userId, Long addressId) {

    public GetAddressQuery {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (addressId == null) {
            throw new IllegalArgumentException("Address ID is required");
        }
    }
}
