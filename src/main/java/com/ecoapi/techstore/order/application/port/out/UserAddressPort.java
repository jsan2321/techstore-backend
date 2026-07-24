package com.ecoapi.techstore.order.application.port.out;

import com.ecoapi.techstore.order.application.port.out.dto.UserAddressData;

import java.util.Optional;

/**
 * Output port for accessing user address information.
 * This port allows the Order context to retrieve user's saved address
 * without direct coupling to the User context's domain model.
 * 
 * Part of the Anti-Corruption Layer (ACL) pattern.
 */
public interface UserAddressPort {
    
    /**
     * Get the default/saved address for a user.
     * 
     * @param userId The user ID to look up
     * @return Optional containing the user's address data, or empty if no address is saved
     */
    Optional<UserAddressData> getUserAddress(Long userId);
    
    /**
     * Check if a user has a saved address
     * 
     * @param userId The user ID to check
     * @return true if the user has a saved address, false otherwise
     */
    boolean hasAddress(Long userId);
}
