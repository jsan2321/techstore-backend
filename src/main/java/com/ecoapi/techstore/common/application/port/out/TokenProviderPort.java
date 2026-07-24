package com.ecoapi.techstore.common.application.port.out;

import java.util.List;

/**
 * Output Port for JWT token generation
 * Infrastructure will provide the actual JWT implementation
 */
public interface TokenProviderPort {
    
    String generateToken(String userId, String email, List<String> roles);
    
    boolean validateToken(String token);
    
    String getUserIdFromToken(String token);

    String getUsernameFromToken(String token);
}
