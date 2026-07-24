package com.ecoapi.techstore.order.application.port.out;

/**
 * Output port for validating user existence and status
 * This port allows the Order context to validate users without direct coupling to the User context
 */
public interface UserValidationPort {
    
    /**
     * Check if a user exists and is active
     * @param userId The user ID to validate
     * @return true if the user exists and is active, false otherwise
     */
    boolean isValidUser(Long userId);
    
    /**
     * Check if a user exists
     * @param userId The user ID to check
     * @return true if the user exists, false otherwise
     */
    boolean userExists(Long userId);
}
