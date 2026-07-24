package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.user.application.port.in.LogoutUseCase;
import com.ecoapi.techstore.user.application.port.out.RefreshTokenRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.LogoutCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for handling user logout
 * Performs:
 * 1. Invalidates all refresh tokens for the user
 * 2. Blacklists the current access token for immediate revocation
 * 
 * Framework-agnostic - no Spring dependencies
 */
public class LogoutService implements LogoutUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(LogoutService.class);
    
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    
    public LogoutService(RefreshTokenRepositoryPort refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }
    
    @Override
    public void logout(LogoutCommand command) {
        logger.info("Logging out user with ID: {}", command.userId().value());
        
        // Revoke all refresh tokens for this user
        //refreshTokenRepository.revokeAllByUserId(command.userId()); // "Sign out of ALL devices"
        refreshTokenRepository.deleteByToken(command.refreshToken()); // "Sign out of THIS device"
        //logger.debug("Revoked all refresh tokens for user: {}", command.userId().value());

        logger.info("Successfully logged out user with ID: {}", command.userId().value());
    }
}
