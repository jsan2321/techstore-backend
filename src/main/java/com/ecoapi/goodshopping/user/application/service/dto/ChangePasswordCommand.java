package com.ecoapi.goodshopping.user.application.service.dto;

/**
 * Command DTO for changing password
 */
public record ChangePasswordCommand(
    Long userId,
    String currentPassword,
    String newPassword
) {
    public ChangePasswordCommand {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (currentPassword == null || currentPassword.isBlank()) {
            throw new IllegalArgumentException("Current password is required");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("New password is required");
        }
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }
}
