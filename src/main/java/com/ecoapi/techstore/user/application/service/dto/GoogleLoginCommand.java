package com.ecoapi.techstore.user.application.service.dto;

/**
 * Command DTO for Google login.
 */
public record GoogleLoginCommand(String idToken) {

    public GoogleLoginCommand {
        if (idToken == null || idToken.isBlank()) {
            throw new IllegalArgumentException("Google ID token is required");
        }
    }
}
