package com.ecoapi.techstore.user.application.port.in;

import com.ecoapi.techstore.user.application.service.dto.RegisterCommand;
import com.ecoapi.techstore.user.domain.model.User;

/**
 * Input Port (Use Case) for registering a new user
 * This defines WHAT the application can do, not HOW
 */
public interface RegisterUserUseCase {
    
    User execute(RegisterCommand command);
}
