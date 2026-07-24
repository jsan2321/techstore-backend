package com.ecoapi.techstore.user.application.service.dto;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;

/**
 * Command to delete a user
 * @param userId The ID of the user to delete
 */
public record DeleteUserCommand(
    UserId userId
) {}
