package com.ecoapi.techstore.product.application.port.in;

import com.ecoapi.techstore.common.application.dto.PagedResult;
import com.ecoapi.techstore.product.application.service.dto.AdminProductListReadModel;
import com.ecoapi.techstore.product.application.service.dto.AdminProductSearchCriteria;

/**
 * Use case for admin product searches.
 * Allows filtering by active status, featured flag, and other admin-specific criteria.
 * Returns admin-specific read models with extended information.
 */
public interface AdminSearchProductsUseCase {

    PagedResult<AdminProductListReadModel> search(AdminProductSearchCriteria criteria);
}
