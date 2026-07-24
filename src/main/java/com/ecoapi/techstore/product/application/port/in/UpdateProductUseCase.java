package com.ecoapi.techstore.product.application.port.in;

import com.ecoapi.techstore.product.application.service.dto.ProductCommand;
import com.ecoapi.techstore.product.domain.model.Product;

public interface UpdateProductUseCase {
    Product updateProduct(Long productId, ProductCommand command);
}
