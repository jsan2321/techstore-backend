package com.ecoapi.techstore.user.application.port.in;

import com.ecoapi.techstore.user.application.service.dto.DeactivateUserCommand;
import com.ecoapi.techstore.user.domain.model.User;

/**
 * Use case for deactivating user accounts.
 */
public interface DeactivateUserUseCase {

    User execute(DeactivateUserCommand command);
}
