package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.user.application.port.in.ChangePasswordUseCase;
import com.ecoapi.techstore.user.application.port.out.PasswordEncoderPort;
import com.ecoapi.techstore.user.application.port.out.RefreshTokenRepositoryPort;
import com.ecoapi.techstore.user.application.port.out.UserEmailNotificationPort;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.ChangePasswordCommand;
import com.ecoapi.techstore.user.domain.exception.InvalidCurrentPasswordException;
import com.ecoapi.techstore.user.domain.exception.UserNotFoundException;
import com.ecoapi.techstore.user.domain.model.User;

/**
 * Service for authenticated password change.
 */
public class ChangePasswordService implements ChangePasswordUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final UserEmailNotificationPort userEmailNotificationPort;

    public ChangePasswordService(
            UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder,
            RefreshTokenRepositoryPort refreshTokenRepository,
            UserEmailNotificationPort userEmailNotificationPort) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userEmailNotificationPort = userEmailNotificationPort;
    }

    @Override
    public void execute(ChangePasswordCommand command) {
        if (!command.newPassword().equals(command.confirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        User user = userRepository.findById(UserId.of(command.userId()))
                .orElseThrow(() -> UserNotFoundException.byId(command.userId()));

        if (!passwordEncoder.matches(command.currentPassword(), user.getPasswordHash())) {
            throw new InvalidCurrentPasswordException();
        }

        if (passwordEncoder.matches(command.newPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("New password must be different from current password");
        }

        String newPasswordHash = passwordEncoder.encode(command.newPassword());
        user.changePassword(newPasswordHash);
        userRepository.save(user);

        refreshTokenRepository.revokeAllByUserId(user.getId());

        userEmailNotificationPort.sendPasswordChangedNotification(
                user.getEmail().value(),
                user.getFirstName()
        );
    }
}
