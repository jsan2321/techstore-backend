package com.ecoapi.goodshopping.user.application.service;

import com.ecoapi.goodshopping.user.application.port.in.GetAllUsersQuery;
import com.ecoapi.goodshopping.user.application.port.in.GetAllUsersUseCase;
import com.ecoapi.goodshopping.user.application.port.out.UserRepositoryPort;
import com.ecoapi.goodshopping.user.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service for retrieving all users
 * Admin only operation
 * 
 * Framework-agnostic - no Spring dependencies
 */
public class GetAllUsersService implements GetAllUsersUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(GetAllUsersService.class);
    
    private final UserRepositoryPort userRepository;
    
    public GetAllUsersService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public List<User> execute(GetAllUsersQuery query) {
        logger.info("Retrieving users - page: {}, size: {}", query.page(), query.size());
        List<User> users = userRepository.findAll(query.page(), query.size());
        logger.info("Retrieved {} users", users.size());
        return users;
    }
}
