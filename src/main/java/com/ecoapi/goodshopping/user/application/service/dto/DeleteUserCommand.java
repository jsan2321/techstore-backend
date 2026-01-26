package com.ecoapi.goodshopping.user.application.service.dto;

import com.ecoapi.goodshopping.common.domain.valueobjects.UserId;

/**
 * Command to delete a user
 * @param userId The ID of the user to delete
 */
public record DeleteUserCommand(
    UserId userId
) {}
