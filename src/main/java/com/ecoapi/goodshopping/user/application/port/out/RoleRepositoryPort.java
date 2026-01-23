package com.ecoapi.goodshopping.user.application.port.out;

import com.ecoapi.goodshopping.user.domain.model.Role;

import java.util.Optional;

/**
 * Output Port for Role persistence
 */
public interface RoleRepositoryPort {
    
    Role save(Role role);
    
    Optional<Role> findByName(String name);
    
    boolean existsByName(String name);
}
