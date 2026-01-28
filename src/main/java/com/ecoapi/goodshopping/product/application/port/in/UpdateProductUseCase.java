package com.ecoapi.goodshopping.product.application.port.in;

import com.ecoapi.goodshopping.product.application.service.dto.UpdateProductCommand;
import com.ecoapi.goodshopping.product.domain.model.Product;

public interface UpdateProductUseCase {
    Product updateProduct(Long productId, UpdateProductCommand command);
}
