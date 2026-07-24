package com.ecoapi.techstore.user.domain.exception;

import com.ecoapi.techstore.common.domain.exceptions.NotFoundException;

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
