package com.ecoapi.goodshopping.user.domain.service;

import com.ecoapi.goodshopping.user.domain.model.User;

/**
 * Domain Service for User-related operations that don't belong to a single entity
 * Pure domain logic, no infrastructure concerns
 */
public class UserDomainService {
    
    /**
     * Validates if a user can be deleted
     * Business rule: A user with active orders cannot be deleted
     */
    public boolean canDeleteUser(User user) {
        // In future: check if user has active orders
        // For now, we can delete any inactive user
        return !user.isActive();
    }
    
    /**
     * Checks if two users have the same email
     */
    public boolean hasSameEmail(User user1, User user2) {
        return user1.getEmail().equals(user2.getEmail());
    }
    
    /**
     * Creates a display name for the user
     */
    public String createDisplayName(User user) {
        return user.getFirstName() + " " + user.getLastName().charAt(0) + ".";
    }
}
