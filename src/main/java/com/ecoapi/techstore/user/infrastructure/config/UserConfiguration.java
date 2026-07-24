package com.ecoapi.techstore.user.infrastructure.config;

import com.ecoapi.techstore.common.application.port.out.TokenProviderPort;
import com.ecoapi.techstore.user.application.port.in.*;
import com.ecoapi.techstore.user.application.port.out.*;
import com.ecoapi.techstore.user.application.service.*;
import com.ecoapi.techstore.user.infrastructure.adapter.output.security.GoogleIdentityProviderAdapter;
import com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${auth.refreshToken.expirationInDays:7}") // Inject here!
    private int refreshTokenExpiryDays;

    @Value("${app.security.email-verification-token-expiration-hours:24}")
    private int emailVerificationTokenExpiryHours;

    @Value("${app.security.password-reset-token-expiration-minutes:60}")
    private int passwordResetTokenExpiryMinutes;

    @Value("${app.security.email-confirmation-resend-cooldown-seconds:60}")
    private long emailConfirmationResendCooldownSeconds;

    @Value("${app.security.password-reset-request-cooldown-seconds:60}")
    private long passwordResetRequestCooldownSeconds;

    @Value("${app.frontend.base-url:http://localhost:4200}")
    private String frontendBaseUrl;

    @Value("${app.security.google.client-id:}")
    private String googleClientId;

    @Value("${app.security.google.token-info-base-url:https://oauth2.googleapis.com}")
    private String googleTokenInfoBaseUrl;
    
    /**
     * Register User Use Case
     * Handles user registration with password encoding and role assignment
     */
    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepositoryPort userRepositoryPort,
            RoleRepositoryPort roleRepositoryPort,
            PasswordEncoderPort passwordEncoderPort,
            UserEventPublisherPort eventPublisherPort,
            EmailVerificationTokenRepositoryPort emailVerificationTokenRepositoryPort,
            UserEmailNotificationPort userEmailNotificationPort) {
        return new RegisterUserService(
                userRepositoryPort,
                roleRepositoryPort,
                passwordEncoderPort,
                eventPublisherPort,
                emailVerificationTokenRepositoryPort,
                userEmailNotificationPort,
                emailVerificationTokenExpiryHours,
                frontendBaseUrl
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
                refreshTokenRepositoryPort,
                refreshTokenExpiryDays
        );
    }

    @Bean
    public GoogleIdentityProviderPort googleIdentityProviderPort() {
        return new GoogleIdentityProviderAdapter(googleClientId, googleTokenInfoBaseUrl);
    }

    @Bean
    public GoogleLoginUseCase googleLoginUseCase(
            GoogleIdentityProviderPort googleIdentityProviderPort,
            UserRepositoryPort userRepositoryPort,
            RoleRepositoryPort roleRepositoryPort,
            PasswordEncoderPort passwordEncoderPort,
            TokenProviderPort tokenProviderPort,
            RefreshTokenRepositoryPort refreshTokenRepositoryPort) {
        return new GoogleLoginService(
                googleIdentityProviderPort,
                userRepositoryPort,
                roleRepositoryPort,
                passwordEncoderPort,
                tokenProviderPort,
                refreshTokenRepositoryPort,
                refreshTokenExpiryDays
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

    @Bean
    public CreateAddressUseCase createAddressUseCase(UserRepositoryPort userRepositoryPort) {
        return new CreateAddressService(userRepositoryPort);
    }

    @Bean
    public ListAddressesUseCase listAddressesUseCase(UserRepositoryPort userRepositoryPort) {
        return new ListAddressesService(userRepositoryPort);
    }

    @Bean
    public GetAddressUseCase getAddressUseCase(UserRepositoryPort userRepositoryPort) {
        return new GetAddressService(userRepositoryPort);
    }

    @Bean
    public UpdateAddressUseCase updateAddressUseCase(UserRepositoryPort userRepositoryPort) {
        return new UpdateAddressService(userRepositoryPort);
    }

    @Bean
    public DeleteAddressUseCase deleteAddressUseCase(UserRepositoryPort userRepositoryPort) {
        return new DeleteAddressService(userRepositoryPort);
    }

    @Bean
    public SetDefaultAddressUseCase setDefaultAddressUseCase(UserRepositoryPort userRepositoryPort) {
        return new SetDefaultAddressService(userRepositoryPort);
    }
    
    /**
     * Logout Use Case
     * Handles user logout and token revocation
     */
    @Bean
    public LogoutUseCase logoutUseCase(RefreshTokenRepositoryPort refreshTokenRepositoryPort) {
        return new LogoutService(refreshTokenRepositoryPort);
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
            RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            OrderLookupPort orderLookupPort) {
        return new DeleteUserService(
                userRepositoryPort,
                refreshTokenRepositoryPort,
                orderLookupPort
        );
    }

    /**
     * Deactivate User Use Case
     * Marks a user as INACTIVE and revokes refresh tokens
     */
    @Bean
    public DeactivateUserUseCase deactivateUserUseCase(
            UserRepositoryPort userRepositoryPort,
            RefreshTokenRepositoryPort refreshTokenRepositoryPort) {
        return new DeactivateUserService(userRepositoryPort, refreshTokenRepositoryPort);
    }

    /**
     * Reactivate User Use Case
     * Marks a user as ACTIVE
     */
    @Bean
    public ReactivateUserUseCase reactivateUserUseCase(
            UserRepositoryPort userRepositoryPort) {
        return new ReactivateUserService(userRepositoryPort);
    }

    @Bean
    public ConfirmEmailUseCase confirmEmailUseCase(
            EmailVerificationTokenRepositoryPort emailVerificationTokenRepositoryPort,
            UserRepositoryPort userRepositoryPort) {
        return new ConfirmEmailService(emailVerificationTokenRepositoryPort, userRepositoryPort);
    }

    @Bean
    public ResendEmailConfirmationUseCase resendEmailConfirmationUseCase(
            UserRepositoryPort userRepositoryPort,
            EmailVerificationTokenRepositoryPort emailVerificationTokenRepositoryPort,
            UserEmailNotificationPort userEmailNotificationPort) {
        return new ResendEmailConfirmationService(
                userRepositoryPort,
                emailVerificationTokenRepositoryPort,
                userEmailNotificationPort,
                emailVerificationTokenExpiryHours,
                emailConfirmationResendCooldownSeconds,
                frontendBaseUrl
        );
    }

    @Bean
    public ChangePasswordUseCase changePasswordUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordEncoderPort passwordEncoderPort,
            RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            UserEmailNotificationPort userEmailNotificationPort) {
        return new ChangePasswordService(
                userRepositoryPort,
                passwordEncoderPort,
                refreshTokenRepositoryPort,
                userEmailNotificationPort
        );
    }

    @Bean
    public RequestPasswordResetUseCase requestPasswordResetUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordResetTokenRepositoryPort passwordResetTokenRepositoryPort,
            UserEmailNotificationPort userEmailNotificationPort) {
        return new RequestPasswordResetService(
                userRepositoryPort,
                passwordResetTokenRepositoryPort,
                userEmailNotificationPort,
                passwordResetTokenExpiryMinutes,
                passwordResetRequestCooldownSeconds,
                frontendBaseUrl
        );
    }

    @Bean
    public ResetPasswordUseCase resetPasswordUseCase(
            PasswordResetTokenRepositoryPort passwordResetTokenRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            PasswordEncoderPort passwordEncoderPort,
            RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            UserEmailNotificationPort userEmailNotificationPort) {
        return new ResetPasswordService(
                passwordResetTokenRepositoryPort,
                userRepositoryPort,
                passwordEncoderPort,
                refreshTokenRepositoryPort,
                userEmailNotificationPort
        );
    }

}
