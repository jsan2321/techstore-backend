package com.ecoapi.techstore.user.application.port.in;

import com.ecoapi.techstore.user.domain.model.User;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;

/**
 * Input Port (Use Case) for retrieving user profile
 */
public interface GetUserProfileUseCase {
    
    User execute(UserId userId);
}
