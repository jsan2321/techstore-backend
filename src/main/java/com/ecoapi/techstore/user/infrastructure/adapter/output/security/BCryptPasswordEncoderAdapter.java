package com.ecoapi.techstore.user.infrastructure.adapter.output.security;

import com.ecoapi.techstore.user.application.port.out.PasswordEncoderPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adapter that implements PasswordEncoderPort using Spring Security's BCrypt
 */
@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoderPort {
    
    private final PasswordEncoder passwordEncoder;
    
    public BCryptPasswordEncoderAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

}
