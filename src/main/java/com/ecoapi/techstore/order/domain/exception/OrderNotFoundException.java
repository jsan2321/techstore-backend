package com.ecoapi.techstore.order.domain.exception;

import com.ecoapi.techstore.common.domain.exceptions.NotFoundException;

/**
 * Exception thrown when an order is not found
 */
public class OrderNotFoundException extends NotFoundException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
