package com.ecoapi.techstore.user.application.port.out;

/**
 * Output Port for password encoding
 * Infrastructure will provide BCrypt or another implementation
 */
public interface PasswordEncoderPort {
    
    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);

}
