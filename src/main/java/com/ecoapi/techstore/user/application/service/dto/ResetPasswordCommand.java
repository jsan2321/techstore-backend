package com.ecoapi.techstore.user.application.service.dto;

/**
 * Command for password reset using a one-time token.
 */
public record ResetPasswordCommand(
        String token,
        String newPassword,
        String confirmPassword) {

    public ResetPasswordCommand {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Reset token is required");
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
