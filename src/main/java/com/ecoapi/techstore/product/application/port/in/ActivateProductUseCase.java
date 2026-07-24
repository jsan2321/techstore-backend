package com.ecoapi.techstore.product.application.port.in;

import com.ecoapi.techstore.product.domain.model.Product;

public interface ActivateProductUseCase {
    Product activateProduct(Long productId);
}
