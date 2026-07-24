package com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.entity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface JpaPasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, String> {

    Optional<PasswordResetTokenEntity> findByToken(String token);

    Optional<PasswordResetTokenEntity> findFirstByUser_IdAndUsedFalseOrderByCreatedAtDesc(Long userId);

    void deleteByUser_IdAndUsedFalse(Long userId);

    @Modifying
    @Query("DELETE FROM PasswordResetTokenEntity t WHERE t.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}
