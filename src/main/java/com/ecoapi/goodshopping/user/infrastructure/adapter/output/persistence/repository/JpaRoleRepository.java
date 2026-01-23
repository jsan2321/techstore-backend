package com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for RoleEntity
 */
interface JpaRoleRepository extends JpaRepository<RoleEntity, Long> {
    
    Optional<RoleEntity> findByName(String name);
    
    boolean existsByName(String name);
}
