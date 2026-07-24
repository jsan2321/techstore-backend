package com.ecoapi.techstore.user.infrastructure.adapter.output.security;

import com.ecoapi.techstore.common.domain.valueobjects.Email;
import com.ecoapi.techstore.user.application.port.out.AuthenticationPort;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.domain.exception.InvalidCredentialsException;
import com.ecoapi.techstore.user.domain.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Adapter that implements AuthenticationPort using Spring Security's AuthenticationManager
 * This provides enterprise-grade authentication with:
 * - Automatic security event publishing (AuthenticationSuccessEvent, AuthenticationFailureEvent)
 * - Account locking and disabling support
 * - Integration with security monitoring tools
 * - Extensibility for LDAP, OAuth2, etc.
 */
@Component
public class SpringAuthenticationAdapter implements AuthenticationPort {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepositoryPort userRepository;
    
    public SpringAuthenticationAdapter(
            AuthenticationManager authenticationManager,
            UserRepositoryPort userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }
    
    @Override
    public User authenticate(String email, String password) {
        try {
            // Create authentication token
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(email, password);
            
            // Delegate to Spring Security's AuthenticationManager
            // This will:
            // 1. Load user via UserDetailsService
            // 2. Verify password via PasswordEncoder
            // 3. Check account status (locked, disabled, expired)
            // 4. Publish security events
            authenticationManager.authenticate(authToken);
            
            // Load and return the domain User entity
            return userRepository.findByEmail(new Email(email))
                    .orElseThrow(InvalidCredentialsException::new);
            
        } catch (BadCredentialsException | DisabledException | LockedException e) {
            // Spring Security automatically publishes AuthenticationFailureBadCredentialsEvent
            // log.warn("Authentication failed: {}", e.getMessage());
            throw new InvalidCredentialsException();
        }
    }
}
