package com.ecoapi.techstore.product.domain.exception;

import com.ecoapi.techstore.common.domain.exceptions.DomainException;

public class ProductAlreadyExistsException extends DomainException {
    public ProductAlreadyExistsException(String message) {
        super(message);
    }
}
