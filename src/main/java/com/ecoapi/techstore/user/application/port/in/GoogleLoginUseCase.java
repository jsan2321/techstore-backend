package com.ecoapi.techstore.user.application.port.in;

import com.ecoapi.techstore.user.application.service.dto.GoogleLoginCommand;
import com.ecoapi.techstore.user.domain.model.AuthenticationResult;

/**
 * Input port for Google login.
 */
public interface GoogleLoginUseCase {

    AuthenticationResult execute(GoogleLoginCommand command);
}
