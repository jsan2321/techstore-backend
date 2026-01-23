package com.ecoapi.goodshopping.user.application.service.dto;

/**
 * Command DTO for user registration
 * Immutable data transfer object
 */
public record RegisterCommand(
    String firstName,
    String lastName,
    String email,
    String password
) {
    public RegisterCommand {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }
}
