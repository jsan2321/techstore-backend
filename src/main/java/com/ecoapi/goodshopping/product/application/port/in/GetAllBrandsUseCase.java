package com.ecoapi.goodshopping.product.application.port.in;

import com.ecoapi.goodshopping.product.domain.model.Brand;
import java.util.List;

/**
 * Use case for getting all brands
 */
public interface GetAllBrandsUseCase {
    List<Brand> execute();
}
