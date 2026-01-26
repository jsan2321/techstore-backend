package com.ecoapi.goodshopping.user.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ecoapi.goodshopping.common.domain.valueobjects.UserId;

/**
 * RefreshToken domain model
 * Represents a long-lived token used to obtain new access tokens
 */
public class RefreshToken {
    
    private String token;
    private UserId userId;
    private LocalDateTime expiryDate;
    private LocalDateTime createdAt;
    private boolean revoked;
    
    private RefreshToken(String token, UserId userId, LocalDateTime expiryDate) {
        this.token = token;
        this.userId = userId;
        this.expiryDate = expiryDate;
        this.createdAt = LocalDateTime.now();
        this.revoked = false;
    }
    
    /**
     * Create a new refresh token
     */
    public static RefreshToken create(UserId userId, int expiryDays) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(expiryDays);
        return new RefreshToken(token, userId, expiryDate);
    }
    
    /**
     * Reconstitute from persistence
     */
    public static RefreshToken reconstitute(String token, UserId userId, 
                                            LocalDateTime expiryDate, 
                                            LocalDateTime createdAt, 
                                            boolean revoked) {
        RefreshToken refreshToken = new RefreshToken(token, userId, expiryDate);
        refreshToken.createdAt = createdAt;
        refreshToken.revoked = revoked;
        return refreshToken;
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
    
    public void revoke() {
        this.revoked = true;
    }
    
    public boolean isValid() {
        return !revoked && !isExpired();
    }
    
    // Getters
    
    public String getToken() {
        return token;
    }
    
    public UserId getUserId() {
        return userId;
    }
    
    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public boolean isRevoked() {
        return revoked;
    }
}
