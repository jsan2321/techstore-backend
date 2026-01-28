package com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.goodshopping.user.application.port.out.RoleRepositoryPort;
import com.ecoapi.goodshopping.user.domain.model.Role;
import com.ecoapi.goodshopping.user.domain.model.RoleName;
import com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.entity.RoleEntity;
import com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter that implements the RoleRepositoryPort
 */
@Component
public class RoleRepositoryAdapter implements RoleRepositoryPort {
    
    private final JpaRoleRepository jpaRepository;
    private final UserPersistenceMapper mapper;
    
    public RoleRepositoryAdapter(JpaRoleRepository jpaRepository, UserPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public Role save(Role role) {
        RoleEntity entity = mapper.toRoleEntity(role);
        RoleEntity savedEntity = jpaRepository.save(entity);
        return mapper.toRoleDomain(savedEntity);
    }
    
    @Override
    public Optional<Role> findByName(RoleName name) {
        Optional<RoleEntity> entity = jpaRepository.findByName(name);
        return entity.map(mapper::toRoleDomain);
    }
    
    @Override
    public boolean existsByName(RoleName name) {
        return jpaRepository.existsByName(name);
    }
}
