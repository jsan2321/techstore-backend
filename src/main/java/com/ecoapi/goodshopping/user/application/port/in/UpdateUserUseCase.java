package com.ecoapi.goodshopping.user.application.port.in;

import com.ecoapi.goodshopping.user.application.service.dto.UpdateUserCommand;
import com.ecoapi.goodshopping.user.domain.model.User;

/**
 * Input Port (Use Case) for updating user profile
 */
public interface UpdateUserUseCase {
    
    User execute(UpdateUserCommand command);
}
