package com.ecoapi.techstore.product.application.service;

import com.ecoapi.techstore.common.application.dto.PagedResult;
import com.ecoapi.techstore.product.application.port.in.AdminSearchProductsUseCase;
import com.ecoapi.techstore.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.techstore.product.application.service.dto.AdminProductListReadModel;
import com.ecoapi.techstore.product.application.service.dto.AdminProductSearchCriteria;

/**
 * Service for admin product searches.
 * Framework-agnostic application service implementing AdminSearchProductsUseCase.
 */
public class AdminSearchProductsService implements AdminSearchProductsUseCase {

    private final ProductRepositoryPort productRepository;

    public AdminSearchProductsService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public PagedResult<AdminProductListReadModel> search(AdminProductSearchCriteria criteria) {
        return productRepository.searchByAdminCriteria(criteria);
    }
}
