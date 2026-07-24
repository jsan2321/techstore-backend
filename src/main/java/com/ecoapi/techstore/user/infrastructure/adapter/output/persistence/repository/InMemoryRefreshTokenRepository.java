package com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.techstore.user.application.port.out.RefreshTokenRepositoryPort;
import com.ecoapi.techstore.user.domain.model.RefreshToken;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of RefreshTokenRepositoryPort
 * 
 * NOTE: This is a simple in-memory implementation for demonstration.
 * For production, you should:
 * 1. Create a RefreshTokenEntity JPA entity
 * 2. Create a JpaRefreshTokenRepository interface
 * 3. Implement proper database persistence
 * 4. Consider using Redis for better performance
 */
@Repository
public class InMemoryRefreshTokenRepository implements RefreshTokenRepositoryPort {
    
    private final Map<String, RefreshToken> tokenStore = new ConcurrentHashMap<>();
    
    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        tokenStore.put(refreshToken.getToken(), refreshToken);
        return refreshToken;
    }
    
    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return Optional.ofNullable(tokenStore.get(token));
    }
    
    @Override
    public void deleteByUserId(UserId userId) {
        tokenStore.entrySet().removeIf(entry -> 
            entry.getValue().getUserId().equals(userId)
        );
    }
    
    @Override
    public void deleteByToken(String token) {
        tokenStore.remove(token);
    }
    
    @Override
    public void revokeAllByUserId(UserId userId) {
        tokenStore.values().stream()
            .filter(token -> token.getUserId().equals(userId))
            .forEach(RefreshToken::revoke);
    }
    
    @Override
    public void deleteExpiredTokens() {
        tokenStore.entrySet().removeIf(entry -> !entry.getValue().isValid());
    }
}
