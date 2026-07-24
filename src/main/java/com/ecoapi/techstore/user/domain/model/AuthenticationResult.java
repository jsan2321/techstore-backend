package com.ecoapi.techstore.user.domain.model;

import com.ecoapi.techstore.user.domain.model.RefreshToken;
import com.ecoapi.techstore.user.domain.model.User;

/**
 * Result DTO for authentication
 * Contains user, access token (JWT), and refresh token
 */
public record AuthenticationResult(
    User user,
    String accessToken,
    RefreshToken refreshToken
) {
    public AuthenticationResult {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("Access token cannot be null or blank");
        }
        
    }
}
