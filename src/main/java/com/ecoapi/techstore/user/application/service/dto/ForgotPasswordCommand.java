package com.ecoapi.techstore.user.application.service.dto;

/**
 * Command for forgot-password requests.
 */
public record ForgotPasswordCommand(String email) {

    public ForgotPasswordCommand {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
    }
}
