package com.ecoapi.goodshopping.user.application.port.in;

import com.ecoapi.goodshopping.user.application.service.dto.DeleteUserCommand;

/**
 * Use Case for deleting a user
 * Admin only operation
 */
public interface DeleteUserUseCase {
    
    /**
     * Delete a user by ID
     * @param command The delete command containing user ID
     */
    void execute(DeleteUserCommand command);
}
