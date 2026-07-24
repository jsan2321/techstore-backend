package com.ecoapi.techstore.cart.application.service.dto;

import java.math.BigDecimal;

public record CartItemDto(
    Long productId,
    String productName,
    int quantity,
    BigDecimal unitPrice,
    BigDecimal totalPrice
) {}
