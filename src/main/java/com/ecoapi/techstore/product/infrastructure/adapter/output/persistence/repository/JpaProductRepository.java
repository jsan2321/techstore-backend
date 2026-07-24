package com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JpaProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
    
    boolean existsByName(String name);

}
