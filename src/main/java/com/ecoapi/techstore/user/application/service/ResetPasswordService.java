package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.user.application.port.in.ResetPasswordUseCase;
import com.ecoapi.techstore.user.application.port.out.PasswordEncoderPort;
import com.ecoapi.techstore.user.application.port.out.PasswordResetTokenRepositoryPort;
import com.ecoapi.techstore.user.application.port.out.RefreshTokenRepositoryPort;
import com.ecoapi.techstore.user.application.port.out.UserEmailNotificationPort;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.ResetPasswordCommand;
import com.ecoapi.techstore.user.domain.exception.InvalidPasswordResetTokenException;
import com.ecoapi.techstore.user.domain.exception.UserNotFoundException;
import com.ecoapi.techstore.user.domain.model.PasswordResetToken;
import com.ecoapi.techstore.user.domain.model.User;

/**
 * Service for resetting password with a one-time token.
 */
public class ResetPasswordService implements ResetPasswordUseCase {

    private final PasswordResetTokenRepositoryPort passwordResetTokenRepository;
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final UserEmailNotificationPort userEmailNotificationPort;

    public ResetPasswordService(
            PasswordResetTokenRepositoryPort passwordResetTokenRepository,
            UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder,
            RefreshTokenRepositoryPort refreshTokenRepository,
            UserEmailNotificationPort userEmailNotificationPort) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userEmailNotificationPort = userEmailNotificationPort;
    }

    @Override
    public void execute(ResetPasswordCommand command) {
        if (!command.newPassword().equals(command.confirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(command.token())
                .orElseThrow(InvalidPasswordResetTokenException::new);

        if (!resetToken.isValid()) {
            throw new InvalidPasswordResetTokenException();
        }

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> UserNotFoundException.byId(resetToken.getUserId().value()));

        if (!user.isActive() || !user.isEmailVerified()) {
            throw new InvalidPasswordResetTokenException();
        }

        if (passwordEncoder.matches(command.newPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("New password must be different from current password");
        }

        String newPasswordHash = passwordEncoder.encode(command.newPassword());
        user.changePassword(newPasswordHash);
        userRepository.save(user);

        refreshTokenRepository.revokeAllByUserId(user.getId());

        resetToken.markAsUsed();
        passwordResetTokenRepository.save(resetToken);
        passwordResetTokenRepository.deleteActiveByUserId(user.getId());

        userEmailNotificationPort.sendPasswordChangedNotification(
                user.getEmail().value(),
                user.getFirstName()
        );
    }
}
