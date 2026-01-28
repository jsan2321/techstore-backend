package com.ecoapi.goodshopping.product.domain.exception;

import com.ecoapi.goodshopping.common.domain.exceptions.DomainException;

public class InsufficientStockException extends DomainException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
