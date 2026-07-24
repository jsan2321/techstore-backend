package com.ecoapi.techstore.cart.application.service.dto;

import com.ecoapi.techstore.cart.domain.model.Cart;
import com.ecoapi.techstore.cart.domain.model.CartItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * Read model for cart data - used for queries
 */
public record CartReadModel(
    Long id,
    Long userId,
    List<CartItemDto> items,
    BigDecimal totalAmount
) {
    /**
     * Factory method to convert from domain model
     */
    public static CartReadModel fromDomain(Cart cart) {
        List<CartItemDto> itemDtos = cart.getItems().stream()
            .map(CartReadModel::toItemDto)
            .toList();
        
        return new CartReadModel(
            cart.getId() != null ? cart.getId().value() : null,
            cart.getUserId() != null ? cart.getUserId().value() : null,
            itemDtos,
            cart.getTotalAmount() != null ? cart.getTotalAmount().value() : BigDecimal.ZERO
        );
    }
    
    private static CartItemDto toItemDto(CartItem item) {
        return new CartItemDto(
            item.getProductId().value(),
            item.getProductName(),
            item.getQuantity(),
            item.getUnitPrice().value(),
            item.getTotalPrice().value()
        );
    }
}
