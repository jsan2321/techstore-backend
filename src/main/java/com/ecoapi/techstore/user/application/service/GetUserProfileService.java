package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.user.application.port.in.GetUserProfileUseCase;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.domain.exception.UserNotFoundException;
import com.ecoapi.techstore.user.domain.model.User;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;

/**
 * Application Service for Getting User Profile
 * Single Responsibility: Retrieve user profile information
 */
public class GetUserProfileService implements GetUserProfileUseCase {
    
    private final UserRepositoryPort userRepository;
    
    public GetUserProfileService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public User execute(UserId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId.value()));
    }
}
