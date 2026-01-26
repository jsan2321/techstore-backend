package com.ecoapi.goodshopping.user.infrastructure.config;

import com.ecoapi.goodshopping.user.application.port.in.*;
import com.ecoapi.goodshopping.user.application.port.out.*;
import com.ecoapi.goodshopping.user.application.service.*;
import com.ecoapi.goodshopping.user.domain.service.TokenBlacklistService;
import com.ecoapi.goodshopping.user.domain.service.UserDomainService;
import com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import com.ecoapi.goodshopping.user.infrastructure.security.jwt.JwtTokenProviderAdapter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Configuration for User Bounded Context
 * This is where we wire everything together
 * Following the Dependency Inversion Principle:
 * - Application layer depends on interfaces (ports)
 * - Infrastructure implements those interfaces
 * - This configuration wires them together
 * 
 * Refactored to use Single Responsibility Principle:
 * - Each use case has its own dedicated service
 * - Services only depend on what they need
 * - Easier to test and maintain
 */
@Configuration
public class UserConfiguration {
    
    /**
     * Configure the persistence mapper
     */
    @Bean
    public UserPersistenceMapper userPersistenceMapper() {
        return new UserPersistenceMapper();
    }
    
    /**
     * Configure the domain service
     */
    @Bean
    public UserDomainService userDomainService() {
        return new UserDomainService();
    }
    
    /**
     * Register User Use Case
     * Handles user registration with password encoding and role assignment
     */
    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepositoryPort userRepositoryPort,
            RoleRepositoryPort roleRepositoryPort,
            PasswordEncoderPort passwordEncoderPort,
            UserEventPublisherPort eventPublisherPort) {
        return new RegisterUserService(
                userRepositoryPort,
                roleRepositoryPort,
                passwordEncoderPort,
                eventPublisherPort
        );
    }
    
    /**
     * Login Use Case
     * Handles authentication via Spring Security's AuthenticationManager
     * and JWT token generation with refresh token support
     */
    @Bean
    public LoginUseCase loginUseCase(
            AuthenticationPort authenticationPort,
            TokenProviderPort tokenProviderPort,
            RefreshTokenRepositoryPort refreshTokenRepositoryPort) {
        return new LoginUserService(
                authenticationPort,
                tokenProviderPort,
                refreshTokenRepositoryPort
        );
    }
    
    /**
     * Get User Profile Use Case
     * Retrieves user information by ID
     */
    @Bean
    public GetUserProfileUseCase getUserProfileUseCase(
            UserRepositoryPort userRepositoryPort) {
        return new GetUserProfileService(userRepositoryPort);
    }
    
    /**
     * Get Current User Use Case
     * Retrieves the currently authenticated user's profile
     */
    @Bean
    public GetCurrentUserUseCase getCurrentUserUseCase(
            UserRepositoryPort userRepositoryPort) {
        return new GetCurrentUserService(userRepositoryPort);
    }
    
    /**
     * Update User Use Case
     * Updates user profile information
     */
    @Bean
    public UpdateUserUseCase updateUserUseCase(
            UserRepositoryPort userRepositoryPort) {
        return new UpdateUserProfileService(userRepositoryPort);
    }
    
    /**
     * Change Password Use Case
     * Handles password changes with verification and event publishing
     */
    @Bean
    public ChangePasswordUseCase changePasswordUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordEncoderPort passwordEncoderPort,
            UserEventPublisherPort eventPublisherPort) {
        return new ChangePasswordService(
                userRepositoryPort,
                passwordEncoderPort,
                eventPublisherPort
        );
    }
    
    /**
     * Logout Use Case
     * Handles user logout and token revocation
     */
    @Bean
    public LogoutUseCase logoutUseCase(
            RefreshTokenRepositoryPort refreshTokenRepositoryPort
            //TokenBlacklistService tokenBlacklistService,
            //JwtTokenProviderAdapter jwtTokenProvider
        ) {
        return new LogoutService(
                refreshTokenRepositoryPort
                //tokenBlacklistService,
                //jwtTokenProvider
        );
    }
    
    /**
     * Refresh Token Use Case
     * Handles access token refresh
     */
    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(
            RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            TokenProviderPort tokenProviderPort) {
        return new RefreshTokenService(
                refreshTokenRepositoryPort,
                userRepositoryPort,
                tokenProviderPort
        );
    }
    
    /**
     * Get All Users Use Case
     * Retrieves all users (Admin only)
     */
    @Bean
    public GetAllUsersUseCase getAllUsersUseCase(
            UserRepositoryPort userRepositoryPort) {
        return new GetAllUsersService(userRepositoryPort);
    }
    
    /**
     * Delete User Use Case
     * Deletes a user (Admin only)
     */
    @Bean
    public DeleteUserUseCase deleteUserUseCase(
            UserRepositoryPort userRepositoryPort,
            RefreshTokenRepositoryPort refreshTokenRepositoryPort) {
        return new DeleteUserService(
                userRepositoryPort,
                refreshTokenRepositoryPort
        );
    }
}
