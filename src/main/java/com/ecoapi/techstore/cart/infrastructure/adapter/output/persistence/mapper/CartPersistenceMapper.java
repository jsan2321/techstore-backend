package com.ecoapi.techstore.cart.infrastructure.adapter.output.persistence.mapper;

import com.ecoapi.techstore.cart.domain.model.Cart;
import com.ecoapi.techstore.cart.domain.model.CartId;
import com.ecoapi.techstore.cart.domain.model.CartItem;
import com.ecoapi.techstore.cart.domain.model.CartItemId;
import com.ecoapi.techstore.cart.infrastructure.adapter.output.persistence.entity.CartEntity;
import com.ecoapi.techstore.cart.infrastructure.adapter.output.persistence.entity.CartItemEntity;
import com.ecoapi.techstore.common.domain.valueobjects.Money;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;

import java.util.List;
import java.util.stream.Collectors;

public class CartPersistenceMapper {
    
    public CartEntity toEntity(Cart cart) {
        CartEntity entity = new CartEntity();
        
        if (cart.getId() != null) {
            entity.setId(cart.getId().value());
        }
        
        entity.setUserId(cart.getUserId().value());
        entity.setTotalAmount(cart.getTotalAmount().value());
        
        List<CartItemEntity> itemEntities = cart.getItems().stream()
                .map(item -> toItemEntity(item, entity))
                .collect(Collectors.toList());
        
        entity.setItems(itemEntities);
        
        return entity;
    }
    
    public Cart toDomain(CartEntity entity) {
        List<CartItem> items = entity.getItems().stream()
                .map(this::toItemDomain)
                .collect(Collectors.toList());
        
        return Cart.reconstitute(
                CartId.of(entity.getId()),
                UserId.of(entity.getUserId()),
                items,
                Money.of(entity.getTotalAmount())
        );
    }
    
    private CartItemEntity toItemEntity(CartItem item, CartEntity cartEntity) {
        CartItemEntity entity = new CartItemEntity();
        
        if (item.getId() != null) {
            entity.setId(item.getId().value());
        }
        
        entity.setCart(cartEntity);
        entity.setProductId(item.getProductId().value());
        entity.setProductName(item.getProductName());
        entity.setProductDescription(item.getProductDescription());
        entity.setProductImageUrl(item.getProductImageUrl());
        entity.setQuantity(item.getQuantity());
        entity.setUnitPrice(item.getUnitPrice().value());
        entity.setTotalPrice(item.getTotalPrice().value());
        
        return entity;
    }
    
    private CartItem toItemDomain(CartItemEntity entity) {
        return new CartItem(
                CartItemId.of(entity.getId()),
                ProductId.of(entity.getProductId()),
                entity.getProductName(),
            entity.getProductDescription(),
            entity.getProductImageUrl(),
                entity.getQuantity(),
                Money.of(entity.getUnitPrice()),
                Money.of(entity.getTotalPrice())
        );
    }
}
