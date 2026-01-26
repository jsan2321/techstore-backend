package com.ecoapi.goodshopping.user.infrastructure.security;

import com.ecoapi.goodshopping.common.domain.valueobjects.Email;
import com.ecoapi.goodshopping.user.application.port.out.UserRepositoryPort;
import com.ecoapi.goodshopping.user.domain.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * UserDetailsService Adapter for Spring Security
 * Bridges the gap between our domain User and Spring Security's UserDetails
 */
@Service
public class UserDetailsServiceAdapter implements UserDetailsService {
    
    private final UserRepositoryPort userRepository;
    
    public UserDetailsServiceAdapter(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(new Email(email))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        return buildUserDetails(user);
    }
    
    private UserDetails buildUserDetails(User user) {
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        
        return new SecurityUser(
                user.getId().value(),
                user.getEmail().value(),
                user.getPasswordHash(),
                user.isActive(),
                true,  // accountNonExpired
                true,  // credentialsNonExpired
                true,  // accountNonLocked
                authorities
        );
    }
}
