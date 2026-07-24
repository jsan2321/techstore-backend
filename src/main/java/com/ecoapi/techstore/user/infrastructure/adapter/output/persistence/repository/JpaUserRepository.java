package com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.techstore.user.domain.model.UserStatus;
import com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Spring Data JPA Repository for UserEntity
 * This is NOT public - it's an internal infrastructure detail
 */
interface JpaUserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    
    Optional<UserEntity> findByEmail(String email);
    
    boolean existsByEmail(String email);

    Page<UserEntity> findByStatus(UserStatus status, Pageable pageable);
}
