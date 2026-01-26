package com.ecoapi.goodshopping.user.domain.service;

import java.time.LocalDateTime;

/**
 * Domain Service for managing blacklisted JWT access tokens.
 * Used for immediate token revocation in cases like logout,
 * password change, or account security issues.
 */
public interface TokenBlacklistService {
    
    /**
     * Add a token to the blacklist until it expires
     * @param token The JWT token to blacklist
     * @param expiryDate The token's expiration date
     */
    void blacklistToken(String token, LocalDateTime expiryDate);
    
    /**
     * Check if a token is blacklisted
     * @param token The JWT token to check
     * @return true if the token is blacklisted, false otherwise
     */
    boolean isTokenBlacklisted(String token);
    
    /**
     * Clean up expired tokens from the blacklist
     */
    void cleanupExpiredTokens();
}
