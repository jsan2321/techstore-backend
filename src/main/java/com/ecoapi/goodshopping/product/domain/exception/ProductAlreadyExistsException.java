package com.ecoapi.goodshopping.product.domain.exception;

import com.ecoapi.goodshopping.common.domain.exceptions.DomainException;

public class ProductAlreadyExistsException extends DomainException {
    public ProductAlreadyExistsException(String message) {
        super(message);
    }
}
