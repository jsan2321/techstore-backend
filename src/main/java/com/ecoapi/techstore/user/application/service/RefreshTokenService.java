package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.common.application.port.out.TokenProviderPort;
import com.ecoapi.techstore.user.application.port.in.RefreshTokenUseCase;
import com.ecoapi.techstore.user.application.port.out.RefreshTokenRepositoryPort;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.RefreshTokenCommand;
import com.ecoapi.techstore.user.domain.exception.InvalidRefreshTokenException;
import com.ecoapi.techstore.user.domain.exception.UserNotFoundException;
import com.ecoapi.techstore.user.domain.model.AuthenticationResult;
import com.ecoapi.techstore.user.domain.model.RefreshToken;
import com.ecoapi.techstore.user.domain.model.User;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for refreshing access tokens
 * 
 * Framework-agnostic - no Spring dependencies
 */
public class RefreshTokenService implements RefreshTokenUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);
    
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final UserRepositoryPort userRepository;
    private final TokenProviderPort tokenProvider;
    
    public RefreshTokenService(RefreshTokenRepositoryPort refreshTokenRepository,
                              UserRepositoryPort userRepository,
                              TokenProviderPort tokenProvider) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }
    
    @Override
    public AuthenticationResult refreshAccessToken(RefreshTokenCommand command) {
        logger.info("Refreshing access token");
        
        // 1. Find and validate refresh token
        RefreshToken refreshToken = refreshTokenRepository.findByToken(command.refreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));
        
        if (!refreshToken.isValid()) {
            logger.warn("Attempted to use invalid or expired refresh token");
            throw new InvalidRefreshTokenException("Refresh token is invalid or expired");
        }
        
        // 2. Get the user
        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found for refresh token"));
        
        if (!user.isActive()) {
            logger.warn("Attempted to refresh token for inactive user: {}", user.getId().value());
            throw new InvalidRefreshTokenException("User account is inactive");
        }
        
        // 3. Generate new access token
        String newAccessToken = tokenProvider.generateToken(
            user.getId().value().toString(),
            user.getEmail().value(),
            user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList())
        );
        
        logger.info("Successfully refreshed access token for user: {}", user.getId().value());
        
        // Return new authentication result with user, new access token, and the same refresh token
        return new AuthenticationResult(user, newAccessToken, refreshToken);
    }
}
