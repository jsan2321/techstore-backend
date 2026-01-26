package com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence;

import com.ecoapi.goodshopping.user.domain.service.TokenBlacklistService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of TokenBlacklistService.
 * Forces you to check a list (in memory) on every single request to see if the token was "logged out."
 * Since it is InMemory, if your server restarts, the blacklist is wiped, and "banned" tokens work again.
 * For production, consider Redis for distributed systems.
 */
//@Service
public class InMemoryTokenBlacklistService implements TokenBlacklistService {
    
    private final Map<String, LocalDateTime> blacklistedTokens = new ConcurrentHashMap<>();
    
    @Override
    public void blacklistToken(String token, LocalDateTime expiryDate) {
        blacklistedTokens.put(token, expiryDate);
    }
    
    @Override
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }
    
    @Override
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}
