package com.ecoapi.techstore.user.application.port.in;

import com.ecoapi.techstore.user.application.service.dto.ConfirmEmailCommand;
import com.ecoapi.techstore.user.domain.model.User;

/**
 * Confirm a user's email from a one-time token.
 */
public interface ConfirmEmailUseCase {

    User execute(ConfirmEmailCommand command);
}
