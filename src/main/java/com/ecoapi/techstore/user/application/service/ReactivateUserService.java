package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.user.application.port.in.ReactivateUserUseCase;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.ReactivateUserCommand;
import com.ecoapi.techstore.user.domain.exception.UserNotFoundException;
import com.ecoapi.techstore.user.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for reactivating user accounts.
 */
public class ReactivateUserService implements ReactivateUserUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ReactivateUserService.class);

    private final UserRepositoryPort userRepository;

    public ReactivateUserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User execute(ReactivateUserCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> UserNotFoundException.byId(command.userId().value()));

        user.reactivate();
        User savedUser = userRepository.save(user);
        logger.info("Reactivated user account with ID: {}", command.userId().value());

        return savedUser;
    }
}
