package com.ecoapi.goodshopping.user.application.port.in;

import com.ecoapi.goodshopping.user.application.service.dto.LogoutCommand;

/**
 * Use Case for user logout
 * Handles:
 * 1. Invalidating refresh tokens
 * 2. Blacklisting access tokens for immediate revocation
 */
public interface LogoutUseCase {
    
    /**
     * Logout a user
     * @param command The logout command containing user ID and tokens
     */
    void logout(LogoutCommand command);
}
