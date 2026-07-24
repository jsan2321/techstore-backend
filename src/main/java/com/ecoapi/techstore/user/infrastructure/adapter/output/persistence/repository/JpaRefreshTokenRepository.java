package com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface JpaRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, String> {
    
    Optional<RefreshTokenEntity> findByToken(String token);
    
    //@Modifying
    //@Query("DELETE FROM RefreshTokenEntity r WHERE r.user.id = :userId")
    void deleteByUser_Id(Long userId);
    
    @Modifying
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
    
    @Modifying
    @Query("UPDATE RefreshTokenEntity r SET r.revoked = true WHERE r.user.id = :userId")
    void revokeAllByUserId(@Param("userId") Long userId);
}
