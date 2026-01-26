package com.ecoapi.goodshopping.user.application.service.dto;

import com.ecoapi.goodshopping.common.domain.valueobjects.Address;
import com.ecoapi.goodshopping.common.domain.valueobjects.PhoneNumber;

/**
 * Command DTO for updating user profile
 */
public record UpdateUserCommand(
    Long userId,
    String firstName,
    String lastName,
    PhoneNumber phoneNumber,
    Address address
) {
    public UpdateUserCommand {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
    }
}
