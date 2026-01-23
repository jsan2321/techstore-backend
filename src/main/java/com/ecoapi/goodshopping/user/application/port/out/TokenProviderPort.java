package com.ecoapi.goodshopping.user.application.port.out;

import com.ecoapi.goodshopping.user.domain.model.User;

/**
 * Output Port for JWT token generation
 * Infrastructure will provide the actual JWT implementation
 */
public interface TokenProviderPort {
    
    String generateToken(User user);
    
    boolean validateToken(String token);
    
    String getUserIdFromToken(String token);
}
