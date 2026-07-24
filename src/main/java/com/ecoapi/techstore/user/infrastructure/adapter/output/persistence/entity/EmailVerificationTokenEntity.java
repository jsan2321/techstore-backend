package com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA entity for email verification token persistence.
 */
@Entity
@Getter
@Setter
@Table(name = "email_verification_tokens")
public class EmailVerificationTokenEntity {

    @Id
    @Column(length = 500)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean used;

    protected EmailVerificationTokenEntity() {
    }

    public EmailVerificationTokenEntity(
            String token,
            UserEntity user,
            LocalDateTime expiryDate,
            LocalDateTime createdAt,
            boolean used) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
        this.createdAt = createdAt;
        this.used = used;
    }
}
