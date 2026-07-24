package com.ecoapi.techstore.user.application.port.in;

import com.ecoapi.techstore.user.application.service.dto.ResendEmailConfirmationCommand;

/**
 * Resend a verification email for a non-verified account.
 */
public interface ResendEmailConfirmationUseCase {

    void execute(ResendEmailConfirmationCommand command);
}
