package com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.mapper;

import com.ecoapi.techstore.common.domain.valueobjects.Email;
import com.ecoapi.techstore.common.domain.valueobjects.PhoneNumber;
import com.ecoapi.techstore.user.domain.model.Role;
import com.ecoapi.techstore.user.domain.model.SavedAddress;
import com.ecoapi.techstore.user.domain.model.User;
import com.ecoapi.techstore.user.domain.model.UserStatus;
import com.ecoapi.techstore.common.domain.valueobjects.RoleId;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.entity.RoleEntity;
import com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.entity.SavedAddressEntity;
import com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.entity.UserEntity;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper to convert between Domain Models and JPA Entities
 * This is THE KEY component in Hexagonal Architecture
 * It keeps the domain pure and infrastructure separate
 */
public class UserPersistenceMapper {

    private final SavedAddressPersistenceMapper addressMapper = new SavedAddressPersistenceMapper();

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

        // Map phoneNumber
        if (user.getPhoneNumber() != null) {
            entity.setPhoneNumber(user.getPhoneNumber().value());
        }

        entity.setStatus(user.getStatus() != null ? user.getStatus() : UserStatus.ACTIVE);
        entity.setEmailVerified(user.isEmailVerified());
        entity.setAccessTokenInvalidBefore(user.getAccessTokenInvalidBefore());

        // Map roles
        Set<RoleEntity> roleEntities = user.getRoles().stream()
                .map(this::toRoleEntity)
                .collect(Collectors.toSet());
        entity.setRoles(roleEntities);

        // Map addresses
        List<SavedAddressEntity> addressEntities = user.getAddressBook().stream()
                .map(address -> addressMapper.toEntity(address, entity))
                .collect(Collectors.toList());
        entity.setAddresses(addressEntities);

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

        UserStatus status = entity.getStatus() != null ? entity.getStatus() : UserStatus.ACTIVE;
        boolean emailVerified = entity.getEmailVerified() == null || entity.getEmailVerified();

        // Map addresses
        List<SavedAddress> addresses = entity.getAddresses().stream()
                .map(addressMapper::toDomain)
                .collect(Collectors.toList());

        return User.reconstitute(
                userId,
                entity.getFirstName(),
                entity.getLastName(),
                email,
                entity.getPasswordHash(),
                phoneNumber,
                roles,
                addresses,
                status,
                emailVerified,
                entity.getAccessTokenInvalidBefore()
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
