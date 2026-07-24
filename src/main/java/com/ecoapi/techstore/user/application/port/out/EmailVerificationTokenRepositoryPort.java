package com.ecoapi.techstore.user.application.port.out;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.user.domain.model.EmailVerificationToken;

import java.util.Optional;

/**
 * Output port for email verification token persistence.
 */
public interface EmailVerificationTokenRepositoryPort {

    EmailVerificationToken save(EmailVerificationToken token);

    Optional<EmailVerificationToken> findByToken(String token);

    Optional<EmailVerificationToken> findLatestActiveByUserId(UserId userId);

    void deleteActiveByUserId(UserId userId);

    void deleteExpiredTokens();
}
