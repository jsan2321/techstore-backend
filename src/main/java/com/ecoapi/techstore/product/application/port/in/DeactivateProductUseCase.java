package com.ecoapi.techstore.product.application.port.in;

import com.ecoapi.techstore.product.domain.model.Product;

public interface DeactivateProductUseCase {
    Product deactivateProduct(Long productId);
}
