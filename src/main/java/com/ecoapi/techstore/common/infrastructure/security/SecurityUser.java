package com.ecoapi.techstore.common.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Custom UserDetails implementation that extends Spring Security's User
 * Adds the user's database ID to the security context
 * This avoids the need to query the database for the user ID on every request
 */
public class SecurityUser extends User {
    
    private final Long userId;
    private final String email;
    
    public SecurityUser(Long userId, 
                       String email, 
                       String password, 
                       Collection<? extends GrantedAuthority> authorities) {
        super(email, password, authorities);
        this.userId = userId;
        this.email = email;
    }
    
    public SecurityUser(Long userId,
                       String email,
                       String password,
                       boolean enabled,
                       boolean accountNonExpired,
                       boolean credentialsNonExpired,
                       boolean accountNonLocked,
                       Collection<? extends GrantedAuthority> authorities) {
        super(email, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
        this.email = email;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public String getEmail() {
        return email;
    }
}
