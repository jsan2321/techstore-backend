package com.ecoapi.techstore.product.application.service;

import com.ecoapi.techstore.common.domain.valueobjects.ProductId;
import com.ecoapi.techstore.product.application.port.in.DeactivateProductUseCase;
import com.ecoapi.techstore.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.techstore.product.domain.exception.ProductNotFoundException;
import com.ecoapi.techstore.product.domain.model.Product;

/**
 * Application Service for deactivating products.
 */
public class DeactivateProductService implements DeactivateProductUseCase {

    private final ProductRepositoryPort productRepository;

    public DeactivateProductService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product deactivateProduct(Long productId) {
        Product product = productRepository.findById(ProductId.of(productId))
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        product.deactivate();
        return productRepository.save(product);
    }
}
