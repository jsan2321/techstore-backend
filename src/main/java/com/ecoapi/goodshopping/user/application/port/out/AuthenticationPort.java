package com.ecoapi.goodshopping.user.application.port.out;

import com.ecoapi.goodshopping.user.domain.model.User;

/**
 * Output Port for authentication operations
 * This abstracts away the authentication mechanism from the application core
 * Infrastructure will provide implementation using Spring Security's AuthenticationManager
 * 
 * Benefits:
 * - Security Events: Spring publishes AuthenticationSuccessEvent, AuthenticationFailureBadCredentialsEvent
 * - Account Locking: Automatic handling of AccountLockedException, DisabledException
 * - Encapsulation: Authentication details (LDAP, OAuth, Database) hidden from core
 * - Audit Logging: Integration with security monitoring tools like Fail2Ban
 */
public interface AuthenticationPort {
    
    /**
     * Authenticate a user with email and password
     * 
     * @param email the user's email
     * @param password the raw password
     * @return the authenticated User
     * @throws InvalidCredentialsException if authentication fails
     */
    User authenticate(String email, String password);
}
