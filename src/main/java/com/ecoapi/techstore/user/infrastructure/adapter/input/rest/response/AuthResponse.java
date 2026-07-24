package com.ecoapi.techstore.user.infrastructure.adapter.input.rest.response;

import com.ecoapi.techstore.user.domain.model.AuthenticationResult;

/**
 * REST response DTO for authentication
 * Contains access token (JWT) and refresh token
 */
public record AuthResponse(
    Long id,
    String email,
    String firstName,
    String lastName,
    String accessToken,
    String refreshToken,
    String tokenType
) {
    public AuthResponse(Long id, String email, String firstName, String lastName, String accessToken, String refreshToken) {
        this(id, email, firstName, lastName, accessToken, refreshToken, "Bearer");
    }
    
    public static AuthResponse from(AuthenticationResult result) {
        return new AuthResponse(
                result.user().getId().value(),
                result.user().getEmail().value(),
                result.user().getFirstName(),
                result.user().getLastName(),
                result.accessToken(),
                result.refreshToken() != null ? result.refreshToken().getToken() : null
        );
    }
}
