package com.ecoapi.techstore.user.application.port.out;

import com.ecoapi.techstore.user.domain.model.RefreshToken;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;

import java.util.Optional;

/**
 * Output Port for RefreshToken persistence operations
 */
public interface RefreshTokenRepositoryPort {
    
    RefreshToken save(RefreshToken refreshToken);
    
    Optional<RefreshToken> findByToken(String token);
    
    void deleteByUserId(UserId userId);
    
    void deleteByToken(String token);
    
    void revokeAllByUserId(UserId userId);
    
    void deleteExpiredTokens();
}
