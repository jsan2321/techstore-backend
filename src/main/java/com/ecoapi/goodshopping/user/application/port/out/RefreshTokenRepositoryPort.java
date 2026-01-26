package com.ecoapi.goodshopping.user.application.port.out;

import com.ecoapi.goodshopping.user.domain.model.RefreshToken;
import com.ecoapi.goodshopping.common.domain.valueobjects.UserId;

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
