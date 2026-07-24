package com.ecoapi.techstore.user.application.service.dto;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;

/**
 * Command to logout a user
 * @param userId The ID of the user logging out
 * @param accessToken The JWT access token to blacklist (optional)
 * @param refreshToken The refresh token to invalidate
 */
public record LogoutCommand(
    UserId userId,
    String refreshToken
) {}
