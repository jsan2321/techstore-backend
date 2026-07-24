package com.ecoapi.techstore.order.domain.exception;

import com.ecoapi.techstore.common.domain.exceptions.DomainException;

public class PaymentProcessingException extends DomainException {
    public PaymentProcessingException(String message) {
        super(message);
    }

    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
