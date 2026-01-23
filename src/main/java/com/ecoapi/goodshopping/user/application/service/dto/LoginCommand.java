package com.ecoapi.goodshopping.user.application.service.dto;

/**
 * Command DTO for user login
 */
public record LoginCommand(
    String email,
    String password
) {
    public LoginCommand {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
    }
}
