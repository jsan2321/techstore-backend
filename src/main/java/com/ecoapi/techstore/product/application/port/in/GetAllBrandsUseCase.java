package com.ecoapi.techstore.product.application.port.in;

import com.ecoapi.techstore.product.domain.model.Brand;
import java.util.List;

/**
 * Use case for getting all brands
 */
public interface GetAllBrandsUseCase {
    List<Brand> execute();
}
