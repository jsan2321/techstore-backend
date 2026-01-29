package com.ecoapi.goodshopping.product.application.port.in;

import com.ecoapi.goodshopping.product.domain.model.Brand;

/**
 * Use case for getting a brand by ID
 */
public interface GetBrandByIdUseCase {
    Brand execute(Long id);
}
