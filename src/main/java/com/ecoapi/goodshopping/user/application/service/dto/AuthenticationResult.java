package com.ecoapi.goodshopping.user.application.service.dto;

import com.ecoapi.goodshopping.user.domain.model.User;

/**
 * Result DTO for authentication
 */
public record AuthenticationResult(
    User user,
    String token
) {
}
