package com.ecoapi.techstore.user.application.service.dto;

/**
 * Command DTO for deleting a saved address.
 */
public record DeleteAddressCommand(Long userId, Long addressId) {

    public DeleteAddressCommand {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (addressId == null) {
            throw new IllegalArgumentException("Address ID is required");
        }
    }
}
