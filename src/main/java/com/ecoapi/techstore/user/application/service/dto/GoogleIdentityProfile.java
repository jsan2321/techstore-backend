package com.ecoapi.techstore.user.application.service.dto;

/**
 * Verified Google identity data.
 */
public record GoogleIdentityProfile(
        String subject,
        String email,
        String givenName,
        String familyName,
        String fullName
) {

    public GoogleIdentityProfile {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Google subject is required");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Google email is required");
        }
    }
}
