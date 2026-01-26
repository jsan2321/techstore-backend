package com.ecoapi.goodshopping.user.application.service;

import com.ecoapi.goodshopping.user.application.port.in.GetUserProfileUseCase;
import com.ecoapi.goodshopping.user.application.port.out.UserRepositoryPort;
import com.ecoapi.goodshopping.user.domain.exception.UserNotFoundException;
import com.ecoapi.goodshopping.user.domain.model.User;
import com.ecoapi.goodshopping.common.domain.valueobjects.UserId;

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
