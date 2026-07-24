package com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.user.application.port.out.PasswordResetTokenRepositoryPort;
import com.ecoapi.techstore.user.domain.model.PasswordResetToken;
import com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.entity.PasswordResetTokenEntity;
import com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * JPA adapter for password reset tokens.
 */
@Repository
@Primary
public class PasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepositoryPort {

    private final JpaPasswordResetTokenRepository jpaRepository;
    private final JpaUserRepository jpaUserRepository;

    public PasswordResetTokenRepositoryAdapter(
            JpaPasswordResetTokenRepository jpaRepository,
            JpaUserRepository jpaUserRepository) {
        this.jpaRepository = jpaRepository;
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    @Transactional
    public PasswordResetToken save(PasswordResetToken token) {
        PasswordResetTokenEntity entity = Objects.requireNonNull(
                toEntity(Objects.requireNonNull(token, "Password reset token cannot be null")),
                "Password reset token entity cannot be null"
        );
        jpaRepository.save(entity);
        return token;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PasswordResetToken> findByToken(String token) {
        return jpaRepository.findByToken(token)
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PasswordResetToken> findLatestActiveByUserId(UserId userId) {
        return jpaRepository.findFirstByUser_IdAndUsedFalseOrderByCreatedAtDesc(userId.value())
                .map(this::toDomain)
                .filter(PasswordResetToken::isValid);
    }

    @Override
    @Transactional
    public void deleteActiveByUserId(UserId userId) {
        jpaRepository.deleteByUser_IdAndUsedFalse(userId.value());
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        jpaRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    private PasswordResetTokenEntity toEntity(PasswordResetToken domain) {
        UserId userId = Objects.requireNonNull(domain.getUserId(), "User ID cannot be null");
        Long userIdValue = Objects.requireNonNull(userId.value(), "User ID value cannot be null");
        UserEntity userRef = jpaUserRepository.getReferenceById(userIdValue);
        return new PasswordResetTokenEntity(
                domain.getToken(),
                userRef,
                domain.getExpiryDate(),
                domain.getCreatedAt(),
                domain.isUsed()
        );
    }

    private PasswordResetToken toDomain(PasswordResetTokenEntity entity) {
        return PasswordResetToken.reconstitute(
                entity.getToken(),
                UserId.of(entity.getUser().getId()),
                entity.getExpiryDate(),
                entity.getCreatedAt(),
                entity.isUsed()
        );
    }
}
