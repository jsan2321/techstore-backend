package com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.mapper;

import com.ecoapi.goodshopping.common.domain.valueobjects.Address;
import com.ecoapi.goodshopping.common.domain.valueobjects.Email;
import com.ecoapi.goodshopping.common.domain.valueobjects.PhoneNumber;
import com.ecoapi.goodshopping.user.domain.model.Role;
import com.ecoapi.goodshopping.user.domain.model.User;
import com.ecoapi.goodshopping.common.domain.valueobjects.RoleId;
import com.ecoapi.goodshopping.common.domain.valueobjects.UserId;
import com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.entity.AddressEmbeddable;
import com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.entity.RoleEntity;
import com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.entity.UserEntity;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper to convert between Domain Models and JPA Entities
 * This is THE KEY component in Hexagonal Architecture
 * It keeps the domain pure and infrastructure separate
 */
public class UserPersistenceMapper {
    
    /**
     * Convert Domain User to JPA UserEntity
     */
    public UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        
        if (user.getId() != null) {
            entity.setId(user.getId().value());
        }
        
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setEmail(user.getEmail().value());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setActive(user.isActive());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        
        // Map phoneNumber
        if (user.getPhoneNumber() != null) {
            entity.setPhoneNumber(user.getPhoneNumber().value());
        }
        
        // Map address
        if (user.getAddress() != null) {
            AddressEmbeddable addressEmbeddable = new AddressEmbeddable(
                    user.getAddress().street(),
                    user.getAddress().city(),
                    user.getAddress().zipCode()
            );
            entity.setAddress(addressEmbeddable);
        }
        
        // Map roles
        Set<RoleEntity> roleEntities = user.getRoles().stream()
                .map(this::toRoleEntity)
                .collect(Collectors.toSet());
        entity.setRoles(roleEntities);
        
        return entity;
    }
    
    /**
     * Convert JPA UserEntity to Domain User
     */
    public User toDomain(UserEntity entity) {
        Email email = new Email(entity.getEmail());
        
        Set<Role> roles = entity.getRoles().stream()
                .map(this::toRoleDomain)
                .collect(Collectors.toSet());
        
        UserId userId = entity.getId() != null ? UserId.of(entity.getId()) : null;
        
        PhoneNumber phoneNumber = entity.getPhoneNumber() != null ? 
                new PhoneNumber(entity.getPhoneNumber()) : null;
        
        Address address = entity.getAddress() != null ? 
                new Address(entity.getAddress().getStreet(), 
                           entity.getAddress().getCity(), 
                           entity.getAddress().getZipCode()) : null;
        
        return User.reconstitute(
                userId,
                entity.getFirstName(),
                entity.getLastName(),
                email,
                entity.getPasswordHash(),
                phoneNumber,
                address,
                entity.isActive(),
                roles,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
    
    /**
     * Convert Domain Role to JPA RoleEntity
     */
    public RoleEntity toRoleEntity(Role role) {
        RoleEntity entity = new RoleEntity();
        
        if (role.getId() != null) {
            entity.setId(role.getId().value());
        }
        
        entity.setName(role.getName());
        
        return entity;
    }
    
    /**
     * Convert JPA RoleEntity to Domain Role
     */
    public Role toRoleDomain(RoleEntity entity) {
        RoleId roleId = entity.getId() != null ? RoleId.of(entity.getId()) : null;
        
        return new Role(roleId, entity.getName());
    }
}
