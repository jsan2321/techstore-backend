package com.ecoapi.techstore.user.domain.model;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * One-time token used to reset a forgotten password.
 */
public class PasswordResetToken {

    private String token;
    private UserId userId;
    private LocalDateTime expiryDate;
    private LocalDateTime createdAt;
    private boolean used;

    private PasswordResetToken(String token, UserId userId, LocalDateTime expiryDate) {
        this.token = token;
        this.userId = userId;
        this.expiryDate = expiryDate;
        this.createdAt = LocalDateTime.now();
        this.used = false;
    }

    public static PasswordResetToken create(UserId userId, int expiryMinutes) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(expiryMinutes);
        return new PasswordResetToken(token, userId, expiryDate);
    }

    public static PasswordResetToken reconstitute(
            String token,
            UserId userId,
            LocalDateTime expiryDate,
            LocalDateTime createdAt,
            boolean used) {
        PasswordResetToken resetToken = new PasswordResetToken(token, userId, expiryDate);
        resetToken.createdAt = createdAt;
        resetToken.used = used;
        return resetToken;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public boolean isValid() {
        return !used && !isExpired();
    }

    public void markAsUsed() {
        this.used = true;
    }

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

    public boolean isUsed() {
        return used;
    }
}
