package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.user.application.port.in.GetAllUsersUseCase;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.GetAllUsersQuery;
import com.ecoapi.techstore.user.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service for retrieving all users
 * Admin only operation
 * 
 */
public class GetAllUsersService implements GetAllUsersUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(GetAllUsersService.class);
    
    private final UserRepositoryPort userRepository;
    
    public GetAllUsersService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public List<User> execute(GetAllUsersQuery query) {
        logger.info("Retrieving users - page: {}, size: {}, status: {}", query.page(), query.size(), query.status());
        List<User> users;

        if (query.status() == null) {
            users = userRepository.findAll(query.page(), query.size());
        } else {
            users = userRepository.findAllByStatus(query.status(), query.page(), query.size());
        }

        logger.info("Retrieved {} users", users.size());
        return users;
    }
}
