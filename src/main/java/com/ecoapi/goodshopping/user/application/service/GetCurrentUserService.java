package com.ecoapi.goodshopping.user.application.service;

import com.ecoapi.goodshopping.common.domain.valueobjects.UserId;
import com.ecoapi.goodshopping.common.infrastructure.security.util.SecurityContextUtil;
import com.ecoapi.goodshopping.user.application.port.in.GetCurrentUserUseCase;
import com.ecoapi.goodshopping.user.application.port.out.UserRepositoryPort;
import com.ecoapi.goodshopping.user.domain.exception.UserNotFoundException;
import com.ecoapi.goodshopping.user.domain.model.User;

/**
 * Service implementation for retrieving the current authenticated user
 * Uses SecurityContextUtil to get user ID from JWT token (no email lookup needed)
 */
public class GetCurrentUserService implements GetCurrentUserUseCase {
    
    private final UserRepositoryPort userRepository;
    
    public GetCurrentUserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public User execute() {
        // Get user ID directly from JWT token (via SecurityContext)
        Long userId = SecurityContextUtil.getCurrentUserId()
                .orElseThrow(() -> new SecurityException("No authenticated user found"));
        
        // Retrieve user from database by ID
        return userRepository.findById(UserId.of(userId))
                .orElseThrow(() -> UserNotFoundException.byId(userId));
    }
}
