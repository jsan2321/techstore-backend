package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.user.application.port.in.ConfirmEmailUseCase;
import com.ecoapi.techstore.user.application.port.out.EmailVerificationTokenRepositoryPort;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.ConfirmEmailCommand;
import com.ecoapi.techstore.user.domain.exception.InvalidEmailVerificationTokenException;
import com.ecoapi.techstore.user.domain.exception.UserNotFoundException;
import com.ecoapi.techstore.user.domain.model.EmailVerificationToken;
import com.ecoapi.techstore.user.domain.model.User;

/**
 * Service for confirming a user's email address.
 */
public class ConfirmEmailService implements ConfirmEmailUseCase {

    private final EmailVerificationTokenRepositoryPort emailVerificationTokenRepository;
    private final UserRepositoryPort userRepository;

    public ConfirmEmailService(
            EmailVerificationTokenRepositoryPort emailVerificationTokenRepository,
            UserRepositoryPort userRepository) {
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public User execute(ConfirmEmailCommand command) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository
                .findByToken(command.token())
                .orElseThrow(InvalidEmailVerificationTokenException::new);

        if (!verificationToken.isValid()) {
            throw new InvalidEmailVerificationTokenException();
        }

        User user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> UserNotFoundException.byId(verificationToken.getUserId().value()));

        if (!user.isEmailVerified()) {
            user.confirmEmail();
            userRepository.save(user);
        }

        verificationToken.markAsUsed();
        emailVerificationTokenRepository.save(verificationToken);

        return user;
    }
}
