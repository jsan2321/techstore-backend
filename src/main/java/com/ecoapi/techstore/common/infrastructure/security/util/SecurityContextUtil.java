package com.ecoapi.techstore.common.infrastructure.security.util;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.common.infrastructure.security.SecurityUser;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Utility class for easily accessing the current authenticated user's information
 * from the SecurityContext without database queries
 */
public final class SecurityContextUtil {
    
    private SecurityContextUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Get the current authenticated user's ID from the SecurityContext
     * @return Optional containing the user ID, empty if not authenticated
     */
    public static Optional<Long> getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }
        
        if (authentication.getPrincipal() instanceof SecurityUser securityUser) {
            return Optional.ofNullable(securityUser.getUserId());
        }
        
        return Optional.empty();
    }
    
    /**
     * Get the current authenticated user's ID as domain UserId object
     * @return Optional containing the UserId, empty if not authenticated
     */
    public static Optional<UserId> getCurrentUserIdAsDomain() {
        return getCurrentUserId().map(UserId::of);
    }
    
    /**
     * Get the current authenticated user's email
     * @return Optional containing the email, empty if not authenticated
     */
    public static Optional<String> getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }
        
        if (authentication.getPrincipal() instanceof SecurityUser securityUser) {
            return Optional.of(securityUser.getEmail());
        }
        
        return Optional.empty();
    }
    
    /**
     * Get the current SecurityUser object
     * @return Optional containing the SecurityUser, empty if not authenticated
     */
    public static Optional<SecurityUser> getCurrentSecurityUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }
        
        if (authentication.getPrincipal() instanceof SecurityUser securityUser) {
            return Optional.of(securityUser);
        }
        
        return Optional.empty();
    }
    
    /**
     * Check if a user is currently authenticated
     * @return true if authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               !authentication.getPrincipal().equals("anonymousUser");
    }
}
