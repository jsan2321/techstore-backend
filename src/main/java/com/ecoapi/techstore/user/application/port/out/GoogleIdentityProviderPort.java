package com.ecoapi.techstore.user.application.port.out;

import com.ecoapi.techstore.user.application.service.dto.GoogleIdentityProfile;

/**
 * Output port for verifying Google ID tokens.
 */
public interface GoogleIdentityProviderPort {

    GoogleIdentityProfile verifyIdToken(String idToken);
}
