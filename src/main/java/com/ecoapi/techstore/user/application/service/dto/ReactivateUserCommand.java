package com.ecoapi.techstore.user.application.service.dto;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;

/**
 * Command to reactivate a user account.
 */
public record ReactivateUserCommand(UserId userId) {
}
