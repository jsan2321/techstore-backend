package com.ecoapi.techstore.product.application.port.in;

import com.ecoapi.techstore.product.domain.model.Brand;

/**
 * Use case for getting a brand by ID
 */
public interface GetBrandByIdUseCase {
    Brand execute(Long id);
}
