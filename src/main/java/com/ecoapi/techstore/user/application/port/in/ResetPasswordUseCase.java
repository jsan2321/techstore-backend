package com.ecoapi.techstore.user.application.port.in;

import com.ecoapi.techstore.user.application.service.dto.ResetPasswordCommand;

/**
 * Reset password using a one-time token.
 */
public interface ResetPasswordUseCase {

    void execute(ResetPasswordCommand command);
}
