package com.ecoapi.techstore.user.application.port.in;

import com.ecoapi.techstore.user.application.service.dto.ForgotPasswordCommand;

/**
 * Request forgot-password flow using an email.
 */
public interface RequestPasswordResetUseCase {

    void execute(ForgotPasswordCommand command);
}
