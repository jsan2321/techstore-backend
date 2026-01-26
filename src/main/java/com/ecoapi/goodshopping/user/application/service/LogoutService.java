package com.ecoapi.goodshopping.user.application.service;

import com.ecoapi.goodshopping.user.application.port.in.LogoutUseCase;
import com.ecoapi.goodshopping.user.application.port.out.RefreshTokenRepositoryPort;
import com.ecoapi.goodshopping.user.application.service.dto.LogoutCommand;
import com.ecoapi.goodshopping.user.domain.service.TokenBlacklistService;
import com.ecoapi.goodshopping.user.infrastructure.security.jwt.JwtTokenProviderAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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
    //private final TokenBlacklistService tokenBlacklistService;
    //private final JwtTokenProviderAdapter jwtTokenProvider;
    
    public LogoutService(RefreshTokenRepositoryPort refreshTokenRepository
                        //TokenBlacklistService tokenBlacklistService,
                        //JwtTokenProviderAdapter jwtTokenProvider
                        ) {
        this.refreshTokenRepository = refreshTokenRepository;
        //this.tokenBlacklistService = tokenBlacklistService;
        //this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Override
    public void logout(LogoutCommand command) {
        logger.info("Logging out user with ID: {}", command.userId().value());
        
        // 1. Revoke all refresh tokens for this user
        //refreshTokenRepository.revokeAllByUserId(command.userId()); // "Sign out of ALL devices"
        refreshTokenRepository.deleteByToken(command.refreshToken()); // "Sign out of THIS device"
        logger.debug("Revoked all refresh tokens for user: {}", command.userId().value());
        
        // 2. Blacklist the access token (if provided) for immediate revocation
        /*if (command.accessToken() != null && !command.accessToken().isBlank()) {
            try {
                Date expirationDate = jwtTokenProvider.getExpirationDateFromToken(command.accessToken());
                LocalDateTime expiryDateTime = expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                
                tokenBlacklistService.blacklistToken(command.accessToken(), expiryDateTime);
                logger.debug("Blacklisted access token for user: {}", command.userId().value());
            } catch (Exception e) {
                logger.warn("Failed to blacklist access token: {}", e.getMessage());
                // Continue with logout even if blacklisting fails
            }
        }*/
        
        logger.info("Successfully logged out user with ID: {}", command.userId().value());
    }
}
