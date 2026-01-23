package com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.goodshopping.common.domain.valueobjects.Email;
import com.ecoapi.goodshopping.user.application.port.out.UserRepositoryPort;
import com.ecoapi.goodshopping.user.domain.model.User;
import com.ecoapi.goodshopping.user.domain.model.UserId;
import com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter that implements the UserRepositoryPort
 * This is where Domain meets Infrastructure
 * It uses the mapper to convert between Domain and JPA entities
 */
@Component
public class UserRepositoryAdapter implements UserRepositoryPort {
    
    private final JpaUserRepository jpaRepository;
    private final UserPersistenceMapper mapper;
    
    public UserRepositoryAdapter(JpaUserRepository jpaRepository, UserPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public User save(User user) {
        // Convert Domain Model to JPA Entity
        UserEntity entity = mapper.toEntity(user);
        
        // Use Spring Data to save
        UserEntity savedEntity = jpaRepository.save(entity);
        
        // Convert back to Domain Model (now with generated ID)
        User savedUser = mapper.toDomain(savedEntity);
        
        return savedUser;
    }
    
    @Override
    public Optional<User> findById(UserId id) {
        Optional<UserEntity> entity = jpaRepository.findById(id.value());
        return entity.map(mapper::toDomain);
    }
    
    @Override
    public Optional<User> findByEmail(Email email) {
        Optional<UserEntity> entity = jpaRepository.findByEmail(email.value());
        return entity.map(mapper::toDomain);
    }
    
    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }
    
    @Override
    public void delete(User user) {
        UserEntity entity = mapper.toEntity(user);
        jpaRepository.delete(entity);
    }
}
