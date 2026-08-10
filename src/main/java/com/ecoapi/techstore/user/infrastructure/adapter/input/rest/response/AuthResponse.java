package com.ecoapi.techstore.user.infrastructure.adapter.input.rest.response;

import com.ecoapi.techstore.user.domain.model.AuthenticationResult;

/**
 * REST response DTO for authentication
 * Contains the short-lived access token. The refresh token is deliberately
 * delivered only as an HttpOnly cookie and is never exposed to JavaScript.
 */
public record AuthResponse(
    Long id,
    String email,
    String firstName,
    String lastName,
    String accessToken,
    String tokenType
) {
    public AuthResponse(Long id, String email, String firstName, String lastName, String accessToken) {
        this(id, email, firstName, lastName, accessToken, "Bearer");
    }
    
    public static AuthResponse from(AuthenticationResult result) {
        return new AuthResponse(
                result.user().getId().value(),
                result.user().getEmail().value(),
                result.user().getFirstName(),
                result.user().getLastName(),
                result.accessToken()
        );
    }
}
