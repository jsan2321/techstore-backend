package com.ecoapi.goodshopping.user.application.port.in;

import com.ecoapi.goodshopping.user.domain.model.User;

/**
 * Input Port (Use Case) for retrieving the currently authenticated user's profile
 */
public interface GetCurrentUserUseCase {
    
    /**
     * Get the profile of the currently authenticated user
     * @return User domain object of the authenticated user
     * @throws com.ecoapi.goodshopping.user.domain.exception.UserNotFoundException if user not found
     * @throws SecurityException if no user is authenticated
     */
    User execute();
}
