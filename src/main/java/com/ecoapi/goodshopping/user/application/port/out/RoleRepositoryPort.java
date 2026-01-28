package com.ecoapi.goodshopping.user.application.port.out;

import com.ecoapi.goodshopping.user.domain.model.Role;
import com.ecoapi.goodshopping.user.domain.model.RoleName;

import java.util.Optional;

/**
 * Output Port for Role persistence
 */
public interface RoleRepositoryPort {
    
    Role save(Role role);
    
    Optional<Role> findByName(RoleName name);
    
    boolean existsByName(RoleName name);
}
