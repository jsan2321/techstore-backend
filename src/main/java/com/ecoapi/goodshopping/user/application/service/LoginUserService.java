package com.ecoapi.goodshopping.user.application.service;

import com.ecoapi.goodshopping.user.application.port.in.LoginUseCase;
import com.ecoapi.goodshopping.user.application.port.out.AuthenticationPort;
import com.ecoapi.goodshopping.user.application.port.out.RefreshTokenRepositoryPort;
import com.ecoapi.goodshopping.user.application.port.out.TokenProviderPort;
import com.ecoapi.goodshopping.user.application.service.dto.LoginCommand;
import com.ecoapi.goodshopping.user.domain.model.AuthenticationResult;
import com.ecoapi.goodshopping.user.domain.model.RefreshToken;
import com.ecoapi.goodshopping.user.domain.model.User;
import org.springframework.beans.factory.annotation.Value;

/**
 * Application Service for User Login
 * Single Responsibility: Handle user authentication and token generation
 * 
 * Uses AuthenticationPort which delegates to Spring Security's AuthenticationManager
 * This provides:
 * - Automatic security event publishing
 * - Account locking support
 * - Integration with monitoring tools
 */
public class LoginUserService implements LoginUseCase {
    
    private final AuthenticationPort authenticationPort;
    private final TokenProviderPort tokenProvider;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    
    @Value("${auth.refreshToken.expirationInDays:7}")
    private int refreshTokenExpiryDays;
    
    public LoginUserService(
            AuthenticationPort authenticationPort,
            TokenProviderPort tokenProvider,
            RefreshTokenRepositoryPort refreshTokenRepository) {
        this.authenticationPort = authenticationPort;
        this.tokenProvider = tokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
    }
    
    @Override
    public AuthenticationResult execute(LoginCommand command) {
        // Delegate authentication to Spring Security via the port
        // This will use UserDetailsService to load the user
        // and PasswordEncoder to verify the password
        User user = authenticationPort.authenticate(command.email(), command.password());
        
        // Generate JWT access token
        String accessToken = tokenProvider.generateToken(user);
        
        // Delete old refresh tokens for this user (optional - for single device only)
        // refreshTokenRepository.deleteByUserId(user.getId());
        
        // Create and save refresh token
        RefreshToken refreshToken = RefreshToken.create(user.getId(), refreshTokenExpiryDays);
        refreshTokenRepository.save(refreshToken);
        
        return new AuthenticationResult(user, accessToken, refreshToken);
    }
}