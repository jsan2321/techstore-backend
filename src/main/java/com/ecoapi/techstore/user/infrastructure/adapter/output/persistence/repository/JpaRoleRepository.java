package com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.techstore.user.domain.model.RoleName;
import com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for RoleEntity
 */
interface JpaRoleRepository extends JpaRepository<RoleEntity, Long> {
    
    Optional<RoleEntity> findByName(RoleName name);
    
    boolean existsByName(RoleName name);
}
