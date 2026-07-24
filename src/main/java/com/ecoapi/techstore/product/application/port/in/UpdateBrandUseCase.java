package com.ecoapi.techstore.product.application.port.in;

import com.ecoapi.techstore.product.domain.model.Brand;

/**
 * Use case for updating a brand
 */
public interface UpdateBrandUseCase {
    Brand updateBrand(Long id, String name);
}
