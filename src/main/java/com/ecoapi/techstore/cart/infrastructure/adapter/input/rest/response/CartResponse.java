package com.ecoapi.techstore.cart.infrastructure.adapter.input.rest.response;

import com.ecoapi.techstore.cart.application.port.out.dto.ProductData;
import com.ecoapi.techstore.cart.domain.model.Cart;
import com.ecoapi.techstore.cart.domain.model.CartItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public record CartResponse(
    Long id,
    Long userId,
    List<ItemResponse> items, 
    BigDecimal totalAmount,
    int itemCount,
    int totalItems
) {
    public static CartResponse fromDomain(Cart cart) {
        return fromDomain(cart, Map.of());
    }

    public static CartResponse fromDomain(Cart cart, Map<Long, ProductData> productDataById) {
    List<ItemResponse> itemResponses = cart.getItems().stream()
        .map(item -> ItemResponse.fromDomain(item, productDataById.get(item.getProductId().value())))
        .toList();

    BigDecimal totalAmount = itemResponses.stream()
        .map(ItemResponse::totalPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, RoundingMode.HALF_UP);

        return new CartResponse(
                cart.getId() != null ? cart.getId().value() : null,
                cart.getUserId().value(),
        itemResponses,
        totalAmount,
                cart.getItemCount(),
                cart.getTotalItems()
        );
    }

    public record ItemResponse(
        Long productId,
        String productName,
        String description,
        String imageUrl,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal originalPrice,
        Integer discountPercentage,
        BigDecimal totalPrice
    ) {
        public static ItemResponse fromDomain(CartItem item) {
            return fromDomain(item, null);
        }

        public static ItemResponse fromDomain(CartItem item, ProductData productData) {
            BigDecimal unitPrice = item.getUnitPrice().value();
            String productName = item.getProductName();
            String description = item.getProductDescription();
            String imageUrl = item.getProductImageUrl();
            BigDecimal originalPrice = unitPrice;
            Integer discountPercentage = null;

            if (productData != null) {
                productName = productData.name();
                description = productData.description();
                imageUrl = productData.imageUrl();
                originalPrice = productData.originalPrice().value();
                unitPrice = productData.effectivePrice().value();
                discountPercentage = productData.discountPercentage();
            }

            BigDecimal totalPrice = unitPrice
                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);

            return new ItemResponse(
                    item.getProductId().value(),
                    productName,
                    description,
                    imageUrl,
                    item.getQuantity(),
                    unitPrice,
                    originalPrice,
                    discountPercentage,
                    totalPrice
            );
        }
    }
}