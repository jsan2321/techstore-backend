package com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest.response;

/**
 * REST response DTO for authentication
 */
public record AuthResponse(
    Long userId,
    String token
) {
}
