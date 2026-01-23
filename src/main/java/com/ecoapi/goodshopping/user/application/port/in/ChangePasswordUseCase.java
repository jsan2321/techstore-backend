package com.ecoapi.goodshopping.user.application.port.in;

import com.ecoapi.goodshopping.user.application.service.dto.ChangePasswordCommand;

/**
 * Input Port (Use Case) for changing user password
 */
public interface ChangePasswordUseCase {
    
    void execute(ChangePasswordCommand command);
}
