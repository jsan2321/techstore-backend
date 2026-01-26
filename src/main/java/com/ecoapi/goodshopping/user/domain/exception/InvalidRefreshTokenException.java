package com.ecoapi.goodshopping.user.domain.exception;

import com.ecoapi.goodshopping.common.domain.exceptions.DomainException;

/**
 * Exception thrown when a refresh token is invalid, expired, or not found
 */
public class InvalidRefreshTokenException extends DomainException {
    
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
