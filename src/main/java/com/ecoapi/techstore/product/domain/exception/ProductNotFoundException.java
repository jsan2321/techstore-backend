package com.ecoapi.techstore.product.domain.exception;

import com.ecoapi.techstore.common.domain.exceptions.NotFoundException;

public class ProductNotFoundException extends NotFoundException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
