package com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.mapper;

import com.ecoapi.techstore.order.domain.model.*;
import com.ecoapi.techstore.order.domain.valueobjects.PaymentTransaction;
import com.ecoapi.techstore.order.domain.valueobjects.ShippingAddress;
import com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.entity.OrderEntity;
import com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.entity.OrderItemEntity;
import com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.entity.PaymentTransactionEmbeddable;
import com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.entity.ShippingAddressEmbeddable;
import com.ecoapi.techstore.common.domain.valueobjects.Money;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;

import java.util.List;
import java.util.stream.Collectors;

public class OrderPersistenceMapper {
    
    public OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        
        if (order.getId() != null) {
            entity.setId(order.getId().getValue());
        }
        
        entity.setUserId(order.getUserId().value());
        entity.setTotalAmount(order.getTotalAmount().value());
        entity.setStatus(order.getStatus());
        entity.setOrderDate(order.getOrderDate());
        entity.setPaymentMethod(order.getPaymentMethod());
        
        // Map shipping address
        if (order.getShippingAddress() != null) {
            entity.setShippingAddress(toShippingAddressEmbeddable(order.getShippingAddress()));
        }

        if (order.getPaymentTransaction() != null) {
            entity.setPaymentTransaction(toPaymentTransactionEmbeddable(order.getPaymentTransaction()));
        }
        
        List<OrderItemEntity> itemEntities = order.getOrderItems().stream()
                .map(item -> toItemEntity(item, entity))
                .collect(Collectors.toList());
        
        entity.setOrderItems(itemEntities);
        
        return entity;
    }
    
    public Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getOrderItems().stream()
                .map(this::toItemDomain)
                .collect(Collectors.toList());
        
        ShippingAddress shippingAddress = null;
        if (entity.getShippingAddress() != null) {
            shippingAddress = toShippingAddressDomain(entity.getShippingAddress());
        }

        PaymentTransaction paymentTransaction = null;
        if (entity.getPaymentTransaction() != null) {
            paymentTransaction = toPaymentTransactionDomain(entity.getPaymentTransaction());
        }
        
        return Order.reconstitute(
                OrderId.of(entity.getId()),
                UserId.of(entity.getUserId()),
                items,
                Money.of(entity.getTotalAmount()),
                entity.getStatus(),
                entity.getOrderDate(),
                shippingAddress,
                entity.getPaymentMethod(),
                paymentTransaction
        );
    }

    private PaymentTransactionEmbeddable toPaymentTransactionEmbeddable(PaymentTransaction transaction) {
        PaymentTransactionEmbeddable embeddable = new PaymentTransactionEmbeddable();
        embeddable.setProvider(transaction.provider());
        embeddable.setProviderOrderId(transaction.providerOrderId());
        embeddable.setProviderCaptureId(transaction.providerCaptureId());
        embeddable.setStatus(transaction.status());
        embeddable.setFailureReason(transaction.failureReason());
        embeddable.setCreatedAt(transaction.createdAt());
        embeddable.setUpdatedAt(transaction.updatedAt());
        return embeddable;
    }

    private PaymentTransaction toPaymentTransactionDomain(PaymentTransactionEmbeddable embeddable) {
        return new PaymentTransaction(
                embeddable.getProvider(),
                embeddable.getProviderOrderId(),
                embeddable.getProviderCaptureId(),
                embeddable.getStatus(),
                embeddable.getFailureReason(),
                embeddable.getCreatedAt(),
                embeddable.getUpdatedAt()
        );
    }
    
    private ShippingAddressEmbeddable toShippingAddressEmbeddable(ShippingAddress address) {
        return new ShippingAddressEmbeddable(
                address.fullName(),
                address.street(),
                address.addressLine2(),
                address.city(),
                address.state(),
                address.postalCode(),
                address.country(),
                address.deliveryNotes()
        );
    }

    private ShippingAddress toShippingAddressDomain(ShippingAddressEmbeddable embeddable) {
        return ShippingAddress.of(
                embeddable.getFullName(),
                embeddable.getStreet(),
                embeddable.getAddressLine2(),
                embeddable.getCity(),
                embeddable.getState(),
                embeddable.getPostalCode(),
                embeddable.getCountry(),
                embeddable.getDeliveryNotes()
        );
    }
    
    private OrderItemEntity toItemEntity(OrderItem item, OrderEntity orderEntity) {
        OrderItemEntity entity = new OrderItemEntity();
        
        if (item.getId() != null) {
            entity.setId(item.getId().getValue());
        }
        
        entity.setOrder(orderEntity);
        entity.setProductId(item.getProductId().value());
        entity.setProductName(item.getProductName());
        entity.setProductDescription(item.getProductDescription());
        entity.setProductImageUrl(item.getProductImageUrl());
        entity.setQuantity(item.getQuantity());
        entity.setUnitPrice(item.getUnitPrice().value());
        entity.setTotalPrice(item.getTotalPrice().value());
        
        return entity;
    }
    
    private OrderItem toItemDomain(OrderItemEntity entity) {
        return new OrderItem(
                OrderItemId.of(entity.getId()),
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
