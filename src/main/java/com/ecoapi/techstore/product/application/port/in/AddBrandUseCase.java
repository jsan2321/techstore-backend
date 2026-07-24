package com.ecoapi.techstore.product.application.port.in;

import com.ecoapi.techstore.product.domain.model.Brand;

/**
 * Use case for adding a new brand
 */
public interface AddBrandUseCase {
    Brand addBrand(String name);
}
