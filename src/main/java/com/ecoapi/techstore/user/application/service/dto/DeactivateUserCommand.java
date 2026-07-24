package com.ecoapi.techstore.user.application.service.dto;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;

/**
 * Command to deactivate a user account.
 */
public record DeactivateUserCommand(UserId userId) {
}
