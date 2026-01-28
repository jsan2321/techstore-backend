package com.ecoapi.goodshopping.product.application.port.in;

import com.ecoapi.goodshopping.product.application.service.dto.ProductCommand;
import com.ecoapi.goodshopping.product.domain.model.Product;

public interface AddProductUseCase {
    Product addProduct(ProductCommand command);
}
