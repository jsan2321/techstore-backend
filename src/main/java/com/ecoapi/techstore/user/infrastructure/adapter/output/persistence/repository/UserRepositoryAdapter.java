package com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.techstore.common.domain.valueobjects.Email;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.domain.model.User;
import com.ecoapi.techstore.user.domain.model.UserStatus;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Transactional
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
    @Transactional(readOnly = true)
    public Optional<User> findById(UserId id) {
        Optional<UserEntity> entity = jpaRepository.findById(Objects.requireNonNull(id.value()));
        return entity.map(mapper::toDomain);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(Email email) {
        Optional<UserEntity> entity = jpaRepository.findByEmail(email.value());
        return entity.map(mapper::toDomain);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }
    
    @Override
    @Transactional
    public void delete(User user) {
        UserEntity entity = mapper.toEntity(user);
        jpaRepository.delete(Objects.requireNonNull(entity));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jpaRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllByStatus(UserStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jpaRepository.findByStatus(status, pageable)
                .getContent()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
