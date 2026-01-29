package com.ecoapi.goodshopping.product.domain.exception;

/**
 * Exception thrown when a brand is not found
 */
public class BrandNotFoundException extends RuntimeException {
    
    public BrandNotFoundException(String message) {
        super(message);
    }
    
    public BrandNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
