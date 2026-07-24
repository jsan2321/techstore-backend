package com.ecoapi.techstore.user.infrastructure.adapter.output.persistence;

import com.ecoapi.techstore.user.application.port.out.RefreshTokenRepositoryPort;
import com.ecoapi.techstore.user.application.port.out.EmailVerificationTokenRepositoryPort;
import com.ecoapi.techstore.user.application.port.out.PasswordResetTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled task to clean up expired tokens
 * Runs periodically to remove expired refresh tokens and blacklisted access tokens
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupScheduler {
    
    //private static final Logger logger = LoggerFactory.getLogger(TokenCleanupScheduler.class);
    
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final EmailVerificationTokenRepositoryPort emailVerificationTokenRepository;
    private final PasswordResetTokenRepositoryPort passwordResetTokenRepository;
    
    /**
     * Clean up expired refresh tokens
     * Runs every hour
     */
    @Scheduled(cron = "0 0 * * * *")  // Every hour at minute 0
    //@Scheduled(cron = "0 0 0 * * ?") // Run every day at midnight
    public void cleanupExpiredRefreshTokens() {
        log.info("Starting scheduled cleanup of expired refresh tokens");
        try {
            refreshTokenRepository.deleteExpiredTokens();
            emailVerificationTokenRepository.deleteExpiredTokens();
            passwordResetTokenRepository.deleteExpiredTokens();
            log.info("Successfully cleaned up expired refresh tokens");
        } catch (Exception e) {
            log.error("Error cleaning up expired refresh tokens", e);
        }
    }
    
}
