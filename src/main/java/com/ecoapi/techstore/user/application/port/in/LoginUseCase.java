package com.ecoapi.techstore.user.application.port.in;

import com.ecoapi.techstore.user.application.service.dto.LoginCommand;
import com.ecoapi.techstore.user.domain.model.AuthenticationResult;

/**
 * Input Port (Use Case) for user authentication
 */
public interface LoginUseCase {
    
    AuthenticationResult execute(LoginCommand command);
}
