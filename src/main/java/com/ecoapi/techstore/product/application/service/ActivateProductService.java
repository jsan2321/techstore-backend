package com.ecoapi.techstore.product.application.service;

import com.ecoapi.techstore.common.domain.valueobjects.ProductId;
import com.ecoapi.techstore.product.application.port.in.ActivateProductUseCase;
import com.ecoapi.techstore.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.techstore.product.domain.exception.ProductNotFoundException;
import com.ecoapi.techstore.product.domain.model.Product;

/**
 * Application Service for activating products.
 */
public class ActivateProductService implements ActivateProductUseCase {

    private final ProductRepositoryPort productRepository;

    public ActivateProductService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product activateProduct(Long productId) {
        Product product = productRepository.findById(ProductId.of(productId))
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        product.activate();
        return productRepository.save(product);
    }
}
