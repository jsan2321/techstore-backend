package com.ecoapi.techstore.user.application.port.in;

import com.ecoapi.techstore.user.application.service.dto.ReactivateUserCommand;
import com.ecoapi.techstore.user.domain.model.User;

/**
 * Use case for reactivating user accounts.
 */
public interface ReactivateUserUseCase {

    User execute(ReactivateUserCommand command);
}
