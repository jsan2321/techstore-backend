package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.common.domain.valueobjects.Email;
import com.ecoapi.techstore.user.application.port.in.ResendEmailConfirmationUseCase;
import com.ecoapi.techstore.user.application.port.out.EmailVerificationTokenRepositoryPort;
import com.ecoapi.techstore.user.application.port.out.UserEmailNotificationPort;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.ResendEmailConfirmationCommand;
import com.ecoapi.techstore.user.domain.model.EmailVerificationToken;
import com.ecoapi.techstore.user.domain.model.User;

import java.time.LocalDateTime;

/**
 * Service for resending verification email to non-verified users.
 */
public class ResendEmailConfirmationService implements ResendEmailConfirmationUseCase {

    private final UserRepositoryPort userRepository;
    private final EmailVerificationTokenRepositoryPort emailVerificationTokenRepository;
    private final UserEmailNotificationPort userEmailNotificationPort;
    private final int emailVerificationExpiryHours;
    private final long resendCooldownSeconds;
    private final String frontendBaseUrl;

    public ResendEmailConfirmationService(
            UserRepositoryPort userRepository,
            EmailVerificationTokenRepositoryPort emailVerificationTokenRepository,
            UserEmailNotificationPort userEmailNotificationPort,
            int emailVerificationExpiryHours,
            long resendCooldownSeconds,
            String frontendBaseUrl) {
        this.userRepository = userRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.userEmailNotificationPort = userEmailNotificationPort;
        this.emailVerificationExpiryHours = emailVerificationExpiryHours;
        this.resendCooldownSeconds = resendCooldownSeconds;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    @Override
    public void execute(ResendEmailConfirmationCommand command) {
        User user = userRepository.findByEmail(new Email(command.email())).orElse(null);

        if (user == null || !user.isActive() || user.isEmailVerified()) {
            return;
        }

        EmailVerificationToken latest = emailVerificationTokenRepository
                .findLatestActiveByUserId(user.getId())
                .orElse(null);

        if (latest != null && latest.getCreatedAt().plusSeconds(resendCooldownSeconds).isAfter(LocalDateTime.now())) {
            return;
        }

        emailVerificationTokenRepository.deleteActiveByUserId(user.getId());

        EmailVerificationToken verificationToken = EmailVerificationToken.create(
                user.getId(),
                emailVerificationExpiryHours
        );
        emailVerificationTokenRepository.save(verificationToken);

        String confirmationLink = frontendBaseUrl + "/auth/confirm-email?token=" + verificationToken.getToken();
        userEmailNotificationPort.sendEmailConfirmation(
                user.getEmail().value(),
                user.getFirstName(),
                confirmationLink
        );
    }
}
