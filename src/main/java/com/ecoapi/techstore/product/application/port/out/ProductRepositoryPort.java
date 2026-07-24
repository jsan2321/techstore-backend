package com.ecoapi.techstore.product.application.port.out;

import com.ecoapi.techstore.common.application.dto.PagedResult;
import com.ecoapi.techstore.product.application.service.dto.AdminProductListReadModel;
import com.ecoapi.techstore.product.application.service.dto.AdminProductSearchCriteria;
import com.ecoapi.techstore.product.application.service.dto.ProductReadModel;
import com.ecoapi.techstore.product.domain.model.Product;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;
import com.ecoapi.techstore.product.application.service.dto.ProductSearchCriteria;

import java.util.Optional;

/**
 * Output Port for Product repository operations
 * Infrastructure will provide the actual implementation
 */
public interface ProductRepositoryPort {
    
    // Write operations (use full Product aggregate)
    Product save(Product product);
    
    void deleteById(ProductId id);
    
    // Read operations for write-side use cases (return full aggregate when needed for business logic)
    Optional<Product> findById(ProductId id);
    
    boolean existsByName(String name);
    
    // Query operations (use read models - CQRS pattern)
    PagedResult<ProductReadModel> searchByCriteria(ProductSearchCriteria criteria);

    // Admin query operations
    PagedResult<AdminProductListReadModel> searchByAdminCriteria(AdminProductSearchCriteria criteria);

}
