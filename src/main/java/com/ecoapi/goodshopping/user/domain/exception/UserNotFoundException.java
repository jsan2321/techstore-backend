package com.ecoapi.goodshopping.user.domain.exception;

import com.ecoapi.goodshopping.common.domain.exceptions.NotFoundException;

/**
 * Exception thrown when a user is not found
 */
public class UserNotFoundException extends NotFoundException {
    
    public UserNotFoundException(String message) {
        super(message);
    }
    
    public static UserNotFoundException byId(Long id) {
        return new UserNotFoundException("User not found with ID: " + id);
    }
    
    public static UserNotFoundException byEmail(String email) {
        return new UserNotFoundException("User not found with email: " + email);
    }
}
