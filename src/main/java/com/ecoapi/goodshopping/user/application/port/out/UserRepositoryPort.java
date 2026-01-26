package com.ecoapi.goodshopping.user.application.port.out;

import com.ecoapi.goodshopping.common.domain.valueobjects.Email;
import com.ecoapi.goodshopping.user.domain.model.User;
import com.ecoapi.goodshopping.common.domain.valueobjects.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Output Port for User persistence
 * This is an interface that the domain/application needs
 * Infrastructure will implement this
 */
public interface UserRepositoryPort {
    
    User save(User user);
    
    Optional<User> findById(UserId id);
    
    Optional<User> findByEmail(Email email);
    
    boolean existsByEmail(Email email);
    
    void delete(User user);
    
    /**
     * Find all users with pagination
     * @param page Page number (0-based)
     * @param size Page size
     * @return List of users
     */
    List<User> findAll(int page, int size);
}
