package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.user.application.port.in.DeactivateUserUseCase;
import com.ecoapi.techstore.user.application.port.out.RefreshTokenRepositoryPort;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.DeactivateUserCommand;
import com.ecoapi.techstore.user.domain.exception.UserNotFoundException;
import com.ecoapi.techstore.user.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for deactivating user accounts.
 */
public class DeactivateUserService implements DeactivateUserUseCase {

    private static final Logger logger = LoggerFactory.getLogger(DeactivateUserService.class);

    private final UserRepositoryPort userRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;

    public DeactivateUserService(UserRepositoryPort userRepository,
                                 RefreshTokenRepositoryPort refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public User execute(DeactivateUserCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> UserNotFoundException.byId(command.userId().value()));

        user.deactivate();
        User savedUser = userRepository.save(user);

        // Revoke refresh tokens immediately so inactive users cannot refresh sessions.
        refreshTokenRepository.deleteByUserId(command.userId());
        logger.info("Deactivated user account with ID: {}", command.userId().value());

        return savedUser;
    }
}
