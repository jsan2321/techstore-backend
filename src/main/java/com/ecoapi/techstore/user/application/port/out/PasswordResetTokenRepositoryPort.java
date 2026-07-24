package com.ecoapi.techstore.user.application.port.out;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.user.domain.model.PasswordResetToken;

import java.util.Optional;

/**
 * Output port for password reset token persistence.
 */
public interface PasswordResetTokenRepositoryPort {

    PasswordResetToken save(PasswordResetToken token);

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findLatestActiveByUserId(UserId userId);

    void deleteActiveByUserId(UserId userId);

    void deleteExpiredTokens();
}
