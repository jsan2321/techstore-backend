package com.ecoapi.techstore.order.domain.exception;

import com.ecoapi.techstore.common.domain.exceptions.DomainException;

/**
 * Exception thrown when an invalid order status transition is attempted
 */
public class InvalidOrderStatusTransitionException extends DomainException {
    public InvalidOrderStatusTransitionException(String message) {
        super(message);
    }
}
