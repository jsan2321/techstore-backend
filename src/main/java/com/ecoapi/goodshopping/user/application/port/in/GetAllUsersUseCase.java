package com.ecoapi.goodshopping.user.application.port.in;

import com.ecoapi.goodshopping.user.domain.model.User;
import java.util.List;

/**
 * Use Case for retrieving all users with pagination
 * Admin only operation
 */
public interface GetAllUsersUseCase {
    
    /**
     * Get all users with pagination
     * @param query The query with page and size
     * @return List of users
     */
    List<User> execute(GetAllUsersQuery query);
}
