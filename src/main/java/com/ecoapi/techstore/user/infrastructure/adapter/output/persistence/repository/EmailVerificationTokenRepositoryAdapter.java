package com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.user.application.port.out.EmailVerificationTokenRepositoryPort;
import com.ecoapi.techstore.user.domain.model.EmailVerificationToken;
import com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.entity.EmailVerificationTokenEntity;
import com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * JPA adapter for email verification tokens.
 */
@Repository
@Primary
public class EmailVerificationTokenRepositoryAdapter implements EmailVerificationTokenRepositoryPort {

    private final JpaEmailVerificationTokenRepository jpaRepository;
    private final JpaUserRepository jpaUserRepository;

    public EmailVerificationTokenRepositoryAdapter(
            JpaEmailVerificationTokenRepository jpaRepository,
            JpaUserRepository jpaUserRepository) {
        this.jpaRepository = jpaRepository;
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    @Transactional
    public EmailVerificationToken save(EmailVerificationToken token) {
        EmailVerificationTokenEntity entity = Objects.requireNonNull(
                toEntity(Objects.requireNonNull(token, "Email verification token cannot be null")),
                "Email verification token entity cannot be null"
        );
        jpaRepository.save(entity);
        return token;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailVerificationToken> findByToken(String token) {
        return jpaRepository.findByToken(token)
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailVerificationToken> findLatestActiveByUserId(UserId userId) {
        return jpaRepository.findFirstByUser_IdAndUsedFalseOrderByCreatedAtDesc(userId.value())
                .map(this::toDomain)
                .filter(EmailVerificationToken::isValid);
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

    private EmailVerificationTokenEntity toEntity(EmailVerificationToken domain) {
        UserId userId = Objects.requireNonNull(domain.getUserId(), "User ID cannot be null");
        Long userIdValue = Objects.requireNonNull(userId.value(), "User ID value cannot be null");
        UserEntity userRef = jpaUserRepository.getReferenceById(userIdValue);
        return new EmailVerificationTokenEntity(
                domain.getToken(),
                userRef,
                domain.getExpiryDate(),
                domain.getCreatedAt(),
                domain.isUsed()
        );
    }

    private EmailVerificationToken toDomain(EmailVerificationTokenEntity entity) {
        return EmailVerificationToken.reconstitute(
                entity.getToken(),
                UserId.of(entity.getUser().getId()),
                entity.getExpiryDate(),
                entity.getCreatedAt(),
                entity.isUsed()
        );
    }
}
