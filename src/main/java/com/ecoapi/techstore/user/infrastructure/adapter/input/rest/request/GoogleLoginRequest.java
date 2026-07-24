package com.ecoapi.techstore.user.infrastructure.adapter.input.rest.request;

import jakarta.validation.constraints.NotBlank;

/**
 * REST request DTO for Google login token exchange.
 */
public record GoogleLoginRequest(
        @NotBlank(message = "Google ID token is required")
        String idToken
) {
}
