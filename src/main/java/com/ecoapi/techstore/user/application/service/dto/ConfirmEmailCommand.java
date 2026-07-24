package com.ecoapi.techstore.user.application.service.dto;

/**
 * Command for email confirmation from verification token.
 */
public record ConfirmEmailCommand(String token) {

    public ConfirmEmailCommand {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Confirmation token is required");
        }
    }
}
