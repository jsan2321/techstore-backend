package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.user.application.port.in.DeleteUserUseCase;
import com.ecoapi.techstore.user.application.port.out.OrderLookupPort;
import com.ecoapi.techstore.user.application.port.out.RefreshTokenRepositoryPort;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.DeleteUserCommand;
import com.ecoapi.techstore.user.domain.exception.UserHasOrdersException;
import com.ecoapi.techstore.user.domain.exception.UserNotFoundException;
import com.ecoapi.techstore.user.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for deleting a user
 * Admin only operation
 * Also deletes all associated refresh tokens
 * 
 * Framework-agnostic - no Spring dependencies
 */
public class DeleteUserService implements DeleteUserUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(DeleteUserService.class);
    
    private final UserRepositoryPort userRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final OrderLookupPort orderLookupPort;
    
    public DeleteUserService(UserRepositoryPort userRepository,
                             RefreshTokenRepositoryPort refreshTokenRepository,
                             OrderLookupPort orderLookupPort) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.orderLookupPort = orderLookupPort;
    }
    
    @Override
    public void execute(DeleteUserCommand command) {
        logger.info("Deleting user with ID: {}", command.userId().value());
        
        // Find user
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with ID: " + command.userId().value()));

        if (orderLookupPort.hasOrders(command.userId())) {
            throw UserHasOrdersException.byUserId(command.userId().value());
        }
        
        // Delete all refresh tokens for this user
        refreshTokenRepository.deleteByUserId(command.userId());
        logger.debug("Deleted all refresh tokens for user: {}", command.userId().value());
        
        // Delete user
        userRepository.delete(user);
        
        logger.info("Successfully deleted user with ID: {}", command.userId().value());
    }
}
