package com.ecoapi.goodshopping.product.application.port.out;

import com.ecoapi.goodshopping.product.application.service.dto.ProductReadModel;
import com.ecoapi.goodshopping.product.domain.model.Product;
import com.ecoapi.goodshopping.product.domain.model.ProductId;
import com.ecoapi.goodshopping.product.domain.model.ProductSearchCriteria;

import java.util.List;
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
    
    boolean existsByNameAndBrandId(String name, Long brandId);
    
    // Query operations (use read models - CQRS pattern)
    List<ProductReadModel> findAllAsReadModel();
    
    List<ProductReadModel> searchByCriteria(ProductSearchCriteria criteria);
    
    Optional<ProductReadModel> findByIdAsReadModel(Long id);
    
    // Deprecated: Legacy methods for backward compatibility - consider removing
    @Deprecated
    List<Product> findAll();
    
    @Deprecated
    List<Product> findByCategory(String categoryName);
    
    @Deprecated
    List<Product> findByBrand(String brand);
    
    @Deprecated
    List<Product> findByCategoryAndBrand(String categoryName, String brand);
    
    @Deprecated
    List<Product> findByName(String name);
    
    @Deprecated
    List<Product> findByBrandAndName(String brand, String name);
}
