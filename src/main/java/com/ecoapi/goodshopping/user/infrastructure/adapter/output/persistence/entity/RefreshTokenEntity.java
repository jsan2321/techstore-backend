package com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA Entity for RefreshToken persistence
 */
@Entity
@Getter
@Setter
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {
    
    @Id
    @Column(length = 500)
    private String token;
    
    // @Column(name = "user_id", nullable = false)
    // private Long userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private boolean revoked;
    
    // @Column(name = "device_info", length = 500)
    // private String deviceInfo;
    
    // @Column(name = "ip_address", length = 45)
    // private String ipAddress;
    
    // JPA requires default constructor
    protected RefreshTokenEntity() {
    }
    
    public RefreshTokenEntity(String token, UserEntity user, LocalDateTime expiryDate, 
                             LocalDateTime createdAt, boolean revoked) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
        this.createdAt = createdAt;
        this.revoked = revoked;
    }
    
 
}
