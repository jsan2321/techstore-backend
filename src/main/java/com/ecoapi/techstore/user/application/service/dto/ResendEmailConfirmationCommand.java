package com.ecoapi.techstore.user.application.service.dto;

/**
 * Command for requesting a new email confirmation link.
 */
public record ResendEmailConfirmationCommand(String email) {

    public ResendEmailConfirmationCommand {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
    }
}
