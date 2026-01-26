package com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence;

import com.ecoapi.goodshopping.user.application.port.out.RefreshTokenRepositoryPort;
import com.ecoapi.goodshopping.user.domain.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final TokenBlacklistService tokenBlacklistService;
    
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
            log.info("Successfully cleaned up expired refresh tokens");
        } catch (Exception e) {
            log.error("Error cleaning up expired refresh tokens", e);
        }
    }
    
    /**
     * Clean up expired blacklisted access tokens
     * Runs every 30 minutes
     */
    @Scheduled(cron = "0 */30 * * * *")  // Every 30 minutes
    public void cleanupExpiredBlacklistedTokens() {
        log.info("Starting scheduled cleanup of expired blacklisted tokens");
        try {
            tokenBlacklistService.cleanupExpiredTokens();
            log.info("Successfully cleaned up expired blacklisted tokens");
        } catch (Exception e) {
            log.error("Error cleaning up expired blacklisted tokens", e);
        }
    }
}
