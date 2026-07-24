package com.ecoapi.techstore.user.domain.exception;

import com.ecoapi.techstore.common.domain.exceptions.DomainException;

/**
 * Exception thrown when attempting to hard-delete a user with existing orders.
 */
public class UserHasOrdersException extends DomainException {

    public UserHasOrdersException(String message) {
        super(message);
    }

    public static UserHasOrdersException byUserId(Long userId) {
        return new UserHasOrdersException("Cannot delete user with existing orders. Deactivate the user instead. User ID: " + userId);
    }
}
