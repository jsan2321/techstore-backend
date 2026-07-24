package com.ecoapi.techstore.product.domain.exception;

import com.ecoapi.techstore.common.domain.exceptions.NotFoundException;

/**
 * Exception thrown when a category is not found
 */
public class CategoryNotFoundException extends NotFoundException {
    
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
