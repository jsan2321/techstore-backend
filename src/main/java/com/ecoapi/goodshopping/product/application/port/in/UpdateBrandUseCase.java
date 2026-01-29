package com.ecoapi.goodshopping.product.application.port.in;

import com.ecoapi.goodshopping.product.domain.model.Brand;

/**
 * Use case for updating a brand
 */
public interface UpdateBrandUseCase {
    Brand updateBrand(Long id, String name);
}
