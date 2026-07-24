package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.common.application.port.out.TokenProviderPort;
import com.ecoapi.techstore.common.domain.valueobjects.Email;
import com.ecoapi.techstore.user.application.port.in.GoogleLoginUseCase;
import com.ecoapi.techstore.user.application.port.out.GoogleIdentityProviderPort;
import com.ecoapi.techstore.user.application.port.out.PasswordEncoderPort;
import com.ecoapi.techstore.user.application.port.out.RefreshTokenRepositoryPort;
import com.ecoapi.techstore.user.application.port.out.RoleRepositoryPort;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.GoogleIdentityProfile;
import com.ecoapi.techstore.user.application.service.dto.GoogleLoginCommand;
import com.ecoapi.techstore.user.domain.exception.InvalidCredentialsException;
import com.ecoapi.techstore.user.domain.model.AuthenticationResult;
import com.ecoapi.techstore.user.domain.model.RefreshToken;
import com.ecoapi.techstore.user.domain.model.Role;
import com.ecoapi.techstore.user.domain.model.RoleName;
import com.ecoapi.techstore.user.domain.model.User;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service for Google ID token exchange login.
 */
public class GoogleLoginService implements GoogleLoginUseCase {

    private static final int MAX_NAME_LENGTH = 50;

    private final GoogleIdentityProviderPort googleIdentityProviderPort;
    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final int refreshTokenExpiryDays;

    public GoogleLoginService(
            GoogleIdentityProviderPort googleIdentityProviderPort,
            UserRepositoryPort userRepository,
            RoleRepositoryPort roleRepository,
            PasswordEncoderPort passwordEncoder,
            TokenProviderPort tokenProvider,
            RefreshTokenRepositoryPort refreshTokenRepository,
            int refreshTokenExpiryDays) {
        this.googleIdentityProviderPort = googleIdentityProviderPort;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenExpiryDays = refreshTokenExpiryDays;
    }

    @Override
    public AuthenticationResult execute(GoogleLoginCommand command) {
        GoogleIdentityProfile profile = googleIdentityProviderPort.verifyIdToken(command.idToken());
        Email email = new Email(profile.email());

        User user = userRepository.findByEmail(email)
                .map(this::ensureExistingUserCanAuthenticate)
                .orElseGet(() -> registerFromGoogleProfile(profile, email));

        String accessToken = tokenProvider.generateToken(
                user.getId().value().toString(),
                user.getEmail().value(),
                user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toList())
        );

        RefreshToken refreshToken = RefreshToken.create(user.getId(), refreshTokenExpiryDays);
        refreshTokenRepository.save(refreshToken);

        return new AuthenticationResult(user, accessToken, refreshToken);
    }

    private User ensureExistingUserCanAuthenticate(User user) {
        if (!user.isActive()) {
            throw new InvalidCredentialsException();
        }

        if (!user.isEmailVerified()) {
            user.confirmEmail();
            return userRepository.save(user);
        }

        return user;
    }

    private User registerFromGoogleProfile(GoogleIdentityProfile profile, Email email) {
        String firstName = resolveFirstName(profile, email);
        String lastName = resolveLastName(profile);
        String generatedPasswordHash = passwordEncoder.encode(UUID.randomUUID().toString());

        User user = User.register(firstName, lastName, email, generatedPasswordHash);
        user.confirmEmail();
        user.addRole(resolveDefaultRole());

        return userRepository.save(user);
    }

    private Role resolveDefaultRole() {
        return roleRepository.findByName(RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_USER)));
    }

    private String resolveFirstName(GoogleIdentityProfile profile, Email email) {
        String candidate = firstNonBlank(profile.givenName(), firstNameFromFullName(profile.fullName()), localPart(email));
        return truncate(candidate, MAX_NAME_LENGTH);
    }

    private String resolveLastName(GoogleIdentityProfile profile) {
        String candidate = firstNonBlank(profile.familyName(), lastNameFromFullName(profile.fullName()), "GoogleUser");
        return truncate(candidate, MAX_NAME_LENGTH);
    }

    private String localPart(Email email) {
        String value = email.value();
        int at = value.indexOf('@');
        return at > 0 ? value.substring(0, at) : value;
    }

    private String firstNameFromFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return null;
        }
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 0 ? parts[0] : null;
    }

    private String lastNameFromFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return null;
        }
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? parts[parts.length - 1] : null;
    }

    private String truncate(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        throw new IllegalArgumentException("At least one non-blank value is required");
    }
}
