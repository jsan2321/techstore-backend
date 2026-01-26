package com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.goodshopping.user.application.port.out.RefreshTokenRepositoryPort;
import com.ecoapi.goodshopping.user.domain.model.RefreshToken;
import com.ecoapi.goodshopping.common.domain.valueobjects.UserId;
import com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.entity.RefreshTokenEntity;
import com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.entity.UserEntity;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * JPA Adapter for RefreshToken persistence
 * Converts between domain RefreshToken and RefreshTokenEntity
 * Marked as @Primary to be the default RefreshTokenRepositoryPort implementation
 */
@Repository
@Primary
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {
    
    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;
     private final JpaUserRepository jpaUserRepository; 
    
    public RefreshTokenRepositoryAdapter(JpaRefreshTokenRepository jpaRefreshTokenRepository, JpaUserRepository jpaUserRepository  ) {
         this.jpaUserRepository = jpaUserRepository;
        this.jpaRefreshTokenRepository = jpaRefreshTokenRepository;
    }
    
    @Override
    @Transactional
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenEntity entity = toEntity(refreshToken);
        jpaRefreshTokenRepository.save(entity);
        return refreshToken;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRefreshTokenRepository.findByToken(token)
                .map(this::toDomain);
    }
    
    @Override
    @Transactional
    public void deleteByUserId(UserId userId) {
        jpaRefreshTokenRepository.deleteByUser_Id(userId.value());
    }
    
    @Override
    @Transactional
    public void deleteByToken(String token) {
        jpaRefreshTokenRepository.deleteById(token);
    }
    
    @Override
    @Transactional
    public void revokeAllByUserId(UserId userId) {
        jpaRefreshTokenRepository.revokeAllByUserId(userId.value());
    }
    
    @Override
    @Transactional
    public void deleteExpiredTokens() {
        jpaRefreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
    
    // Mapping methods
    
    private RefreshTokenEntity toEntity(RefreshToken domain) {

        // TRICK: getReferenceById creates a proxy (doesn't hit DB) just for linking
        UserEntity userRef = jpaUserRepository.getReferenceById(domain.getUserId().value());

        return new RefreshTokenEntity(
                domain.getToken(),
                userRef,
                domain.getExpiryDate(),
                domain.getCreatedAt(),
                domain.isRevoked()
        );
    }
    
    private RefreshToken toDomain(RefreshTokenEntity entity) {
        return RefreshToken.reconstitute(
                entity.getToken(),
                UserId.of(entity.getUser().getId()),
                entity.getExpiryDate(),
                entity.getCreatedAt(),
                entity.isRevoked()
        );
    }
}
