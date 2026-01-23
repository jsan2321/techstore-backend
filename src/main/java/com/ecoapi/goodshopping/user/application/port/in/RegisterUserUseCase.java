package com.ecoapi.goodshopping.user.application.port.in;

import com.ecoapi.goodshopping.user.application.service.dto.RegisterCommand;
import com.ecoapi.goodshopping.user.domain.model.User;

/**
 * Input Port (Use Case) for registering a new user
 * This defines WHAT the application can do, not HOW
 */
public interface RegisterUserUseCase {
    
    User execute(RegisterCommand command);
}
