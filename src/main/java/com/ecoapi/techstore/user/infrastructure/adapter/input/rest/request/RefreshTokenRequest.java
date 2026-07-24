package com.ecoapi.techstore.user.infrastructure.adapter.input.rest.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {}