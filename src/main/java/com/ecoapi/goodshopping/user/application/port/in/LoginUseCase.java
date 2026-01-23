package com.ecoapi.goodshopping.user.application.port.in;

import com.ecoapi.goodshopping.user.application.service.dto.LoginCommand;
import com.ecoapi.goodshopping.user.application.service.dto.AuthenticationResult;

/**
 * Input Port (Use Case) for user authentication
 */
public interface LoginUseCase {
    
    AuthenticationResult execute(LoginCommand command);
}
