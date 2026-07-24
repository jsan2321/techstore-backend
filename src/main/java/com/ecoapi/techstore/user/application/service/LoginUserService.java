package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.common.application.port.out.TokenProviderPort;
import com.ecoapi.techstore.user.application.port.in.LoginUseCase;
import com.ecoapi.techstore.user.application.port.out.AuthenticationPort;
import com.ecoapi.techstore.user.application.port.out.RefreshTokenRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.LoginCommand;
import com.ecoapi.techstore.user.domain.exception.EmailNotVerifiedException;
import com.ecoapi.techstore.user.domain.model.AuthenticationResult;
import com.ecoapi.techstore.user.domain.model.RefreshToken;
import com.ecoapi.techstore.user.domain.model.User;

import java.util.stream.Collectors;

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
    private final int refreshTokenExpiryDays;
        
    public LoginUserService(
            AuthenticationPort authenticationPort,
            TokenProviderPort tokenProvider,
            RefreshTokenRepositoryPort refreshTokenRepository,
            int refreshTokenExpiryDays) {
        this.authenticationPort = authenticationPort;
        this.tokenProvider = tokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenExpiryDays = refreshTokenExpiryDays;
    }
    
    @Override
    public AuthenticationResult execute(LoginCommand command) {
        // Delegate authentication to Spring Security via the port
        // This will use UserDetailsService to load the user
        // and PasswordEncoder to verify the password
        User user = authenticationPort.authenticate(command.email(), command.password());

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException(user.getEmail().value());
        }
        
        // Generate JWT access token
        String accessToken = tokenProvider.generateToken(
                user.getId().value().toString(),
                user.getEmail().value(),
                user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toList())
        );
        
        // Delete old refresh tokens for this user (optional - for single device only)
        // refreshTokenRepository.deleteByUserId(user.getId());
        
        // Create and save refresh token
        RefreshToken refreshToken = RefreshToken.create(user.getId(), refreshTokenExpiryDays);
        refreshTokenRepository.save(refreshToken);
        
        return new AuthenticationResult(user, accessToken, refreshToken);
    }
}