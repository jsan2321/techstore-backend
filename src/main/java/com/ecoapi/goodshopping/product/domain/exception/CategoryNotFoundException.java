package com.ecoapi.goodshopping.product.domain.exception;

/**
 * Exception thrown when a category is not found
 */
public class CategoryNotFoundException extends RuntimeException {
    
    public CategoryNotFoundException(String message) {
        super(message);
    }
    
    public CategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
