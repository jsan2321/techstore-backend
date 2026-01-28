package com.ecoapi.goodshopping.product.domain.exception;

import com.ecoapi.goodshopping.common.domain.exceptions.NotFoundException;

public class ProductNotFoundException extends NotFoundException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
