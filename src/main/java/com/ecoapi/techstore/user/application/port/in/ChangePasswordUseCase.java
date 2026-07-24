package com.ecoapi.techstore.user.application.port.in;

import com.ecoapi.techstore.user.application.service.dto.ChangePasswordCommand;

/**
 * Change password for the authenticated user.
 */
public interface ChangePasswordUseCase {

    void execute(ChangePasswordCommand command);
}
