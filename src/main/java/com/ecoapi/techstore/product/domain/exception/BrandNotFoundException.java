package com.ecoapi.techstore.product.domain.exception;

import com.ecoapi.techstore.common.domain.exceptions.NotFoundException;

/**
 * Exception thrown when a brand is not found
 */
public class BrandNotFoundException extends NotFoundException {
    
    public BrandNotFoundException(String message) {
        super(message);
    }
}
