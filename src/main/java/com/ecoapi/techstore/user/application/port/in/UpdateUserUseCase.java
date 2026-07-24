package com.ecoapi.techstore.user.application.port.in;

import com.ecoapi.techstore.user.application.service.dto.UpdateUserCommand;
import com.ecoapi.techstore.user.domain.model.User;

/**
 * Input Port (Use Case) for updating user profile
 */
public interface UpdateUserUseCase {
    
    User execute(UpdateUserCommand command);
}
