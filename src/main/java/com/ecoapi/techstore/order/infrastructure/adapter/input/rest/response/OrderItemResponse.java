package com.ecoapi.techstore.order.infrastructure.adapter.input.rest.response;

import com.ecoapi.techstore.order.domain.model.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(
    Long productId,
    String productName,
    String description,
    String imageUrl,
    int quantity,
    BigDecimal unitPrice,
    BigDecimal totalPrice
) {
    public static OrderItemResponse fromDomain(OrderItem item) {
        return new OrderItemResponse(
                item.getProductId().value(),
                item.getProductName(),
                item.getProductDescription(),
                item.getProductImageUrl(),
                item.getQuantity(),
                item.getUnitPrice().value(),
                item.getTotalPrice().value()
        );
    }
}
