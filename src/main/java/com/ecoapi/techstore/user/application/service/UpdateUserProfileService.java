package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.user.application.port.in.UpdateUserUseCase;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.UpdateUserCommand;
import com.ecoapi.techstore.user.domain.exception.UserNotFoundException;
import com.ecoapi.techstore.user.domain.model.User;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;

/**
 * Application Service for Updating User Profile
 * Single Responsibility: Handle user profile updates
 */
public class UpdateUserProfileService implements UpdateUserUseCase {
    
    private final UserRepositoryPort userRepository;
    
    public UpdateUserProfileService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public User execute(UpdateUserCommand command) {
        UserId userId = UserId.of(command.userId());
        
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(command.userId()));
        
        // Update profile using domain method
        user.updateProfile(command.firstName(), command.lastName(), command.phoneNumber());
        
        // Save changes
        return userRepository.save(user);
    }
}
