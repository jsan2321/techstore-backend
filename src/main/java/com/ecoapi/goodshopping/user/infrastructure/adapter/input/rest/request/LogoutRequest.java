package com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request for user logout
 */
public record LogoutRequest(
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {}
