package com.ecoapi.goodshopping.product.application.port.in;

import com.ecoapi.goodshopping.product.domain.model.Brand;

/**
 * Use case for adding a new brand
 */
public interface AddBrandUseCase {
    Brand addBrand(String name);
}
