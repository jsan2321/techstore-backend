package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.common.domain.valueobjects.Email;
import com.ecoapi.techstore.user.application.port.in.RequestPasswordResetUseCase;
import com.ecoapi.techstore.user.application.port.out.PasswordResetTokenRepositoryPort;
import com.ecoapi.techstore.user.application.port.out.UserEmailNotificationPort;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.ForgotPasswordCommand;
import com.ecoapi.techstore.user.domain.model.PasswordResetToken;
import com.ecoapi.techstore.user.domain.model.User;

import java.time.LocalDateTime;

/**
 * Service for forgot-password requests.
 */
public class RequestPasswordResetService implements RequestPasswordResetUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordResetTokenRepositoryPort passwordResetTokenRepository;
    private final UserEmailNotificationPort userEmailNotificationPort;
    private final int resetTokenExpiryMinutes;
    private final long resetRequestCooldownSeconds;
    private final String frontendBaseUrl;

    public RequestPasswordResetService(
            UserRepositoryPort userRepository,
            PasswordResetTokenRepositoryPort passwordResetTokenRepository,
            UserEmailNotificationPort userEmailNotificationPort,
            int resetTokenExpiryMinutes,
            long resetRequestCooldownSeconds,
            String frontendBaseUrl) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userEmailNotificationPort = userEmailNotificationPort;
        this.resetTokenExpiryMinutes = resetTokenExpiryMinutes;
        this.resetRequestCooldownSeconds = resetRequestCooldownSeconds;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    @Override
    public void execute(ForgotPasswordCommand command) {
        User user = userRepository.findByEmail(new Email(command.email())).orElse(null);

        // Intentionally silent to avoid leaking account existence.
        if (user == null || !user.isActive() || !user.isEmailVerified()) {
            return;
        }

        PasswordResetToken latest = passwordResetTokenRepository
                .findLatestActiveByUserId(user.getId())
                .orElse(null);

        if (latest != null && latest.getCreatedAt().plusSeconds(resetRequestCooldownSeconds).isAfter(LocalDateTime.now())) {
            return;
        }

        passwordResetTokenRepository.deleteActiveByUserId(user.getId());

        PasswordResetToken resetToken = PasswordResetToken.create(user.getId(), resetTokenExpiryMinutes);
        passwordResetTokenRepository.save(resetToken);

        String resetLink = frontendBaseUrl + "/auth/reset-password?token=" + resetToken.getToken();
        userEmailNotificationPort.sendPasswordReset(
                user.getEmail().value(),
                user.getFirstName(),
                resetLink
        );
    }
}
