package com.ecoapi.techstore.user.application.service.dto;

/**
 * Command for authenticated password change.
 */
public record ChangePasswordCommand(
        Long userId,
        String currentPassword,
        String newPassword,
        String confirmPassword) {

    public ChangePasswordCommand {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
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
        if (confirmPassword == null || confirmPassword.isBlank()) {
            throw new IllegalArgumentException("Confirm password is required");
        }
    }
}
