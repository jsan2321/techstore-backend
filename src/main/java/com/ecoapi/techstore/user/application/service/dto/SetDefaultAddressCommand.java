package com.ecoapi.techstore.user.application.service.dto;

/**
 * Command DTO for setting a saved address as default.
 */
public record SetDefaultAddressCommand(Long userId, Long addressId) {

    public SetDefaultAddressCommand {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (addressId == null) {
            throw new IllegalArgumentException("Address ID is required");
        }
    }
}
