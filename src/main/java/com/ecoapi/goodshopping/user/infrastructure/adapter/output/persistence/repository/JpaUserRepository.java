package com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for UserEntity
 * This is NOT public - it's an internal infrastructure detail
 */
interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    
    Optional<UserEntity> findByEmail(String email);
    
    boolean existsByEmail(String email);
}
