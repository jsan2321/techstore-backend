package com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest.response;

import com.ecoapi.goodshopping.user.domain.model.AuthenticationResult;

/**
 * REST response DTO for authentication
 * Contains access token (JWT) and refresh token
 */
public record AuthResponse(
    Long userId,
    String email,
    String firstName,
    String lastName,
    String accessToken,
    String refreshToken,
    String tokenType
) {
    public AuthResponse(Long userId, String email, String firstName, String lastName, String accessToken, String refreshToken) {
        this(userId, email, firstName, lastName, accessToken, refreshToken, "Bearer");
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
