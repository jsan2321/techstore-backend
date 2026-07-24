package com.ecoapi.techstore.order.infrastructure.adapter.output.user;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.order.application.port.out.UserValidationPort;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Infrastructure adapter for user validation
 * Implements the UserValidationPort by delegating to the User context's repository
 */
public class UserValidationAdapter implements UserValidationPort {
    
    private static final Logger logger = LoggerFactory.getLogger(UserValidationAdapter.class);
    
    private final UserRepositoryPort userRepository;
    
    public UserValidationAdapter(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public boolean isValidUser(Long userId) {
        try {
            Optional<User> user = userRepository.findById(UserId.of(userId));
            if (user.isEmpty()) {
                logger.debug("User not found with id: {}", userId);
                return false;
            }

            return user.get().isActive();
        } catch (Exception e) {
            logger.error("Error validating user {}: {}", userId, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean userExists(Long userId) {
        try {
            return userRepository.findById(UserId.of(userId)).isPresent();
        } catch (Exception e) {
            logger.error("Error checking user existence {}: {}", userId, e.getMessage());
            return false;
        }
    }
}
