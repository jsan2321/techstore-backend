package com.ecoapi.goodshopping.user.application.service.dto;

/**
 * Command to refresh an access token
 * @param refreshToken The refresh token to use
 */
public record RefreshTokenCommand(
    String refreshToken
) {}
