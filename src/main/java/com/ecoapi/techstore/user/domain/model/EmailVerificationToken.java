package com.ecoapi.techstore.user.domain.model;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * One-time token used to confirm a user's email address.
 */
public class EmailVerificationToken {

    private String token;
    private UserId userId;
    private LocalDateTime expiryDate;
    private LocalDateTime createdAt;
    private boolean used;

    private EmailVerificationToken(String token, UserId userId, LocalDateTime expiryDate) {
        this.token = token;
        this.userId = userId;
        this.expiryDate = expiryDate;
        this.createdAt = LocalDateTime.now();
        this.used = false;
    }

    public static EmailVerificationToken create(UserId userId, int expiryHours) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(expiryHours);
        return new EmailVerificationToken(token, userId, expiryDate);
    }

    public static EmailVerificationToken reconstitute(
            String token,
            UserId userId,
            LocalDateTime expiryDate,
            LocalDateTime createdAt,
            boolean used) {
        EmailVerificationToken verificationToken = new EmailVerificationToken(token, userId, expiryDate);
        verificationToken.createdAt = createdAt;
        verificationToken.used = used;
        return verificationToken;
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
