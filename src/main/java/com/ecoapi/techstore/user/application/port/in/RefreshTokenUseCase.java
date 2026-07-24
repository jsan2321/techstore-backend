package com.ecoapi.techstore.user.application.port.in;

import com.ecoapi.techstore.user.application.service.dto.RefreshTokenCommand;
import com.ecoapi.techstore.user.domain.model.AuthenticationResult;

/**
 * Use Case for refreshing access tokens using a refresh token
 */
public interface RefreshTokenUseCase {
    
    /**
     * Generate a new access token using a refresh token
     * @param command The refresh token command
     * @return A new authentication result with a fresh access token
     */
    AuthenticationResult refreshAccessToken(RefreshTokenCommand command);
}
