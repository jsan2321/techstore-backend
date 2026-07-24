package com.ecoapi.techstore.order.domain.events;

import com.ecoapi.techstore.common.domain.events.DomainEvent;
import com.ecoapi.techstore.order.domain.model.OrderId;
import com.ecoapi.techstore.order.domain.model.OrderStatus;

import java.time.LocalDateTime;

public record OrderStatusChangedEvent(
    OrderId orderId,
    OrderStatus oldStatus,
    OrderStatus newStatus,
    LocalDateTime occurredOn
) implements DomainEvent {
    
    public OrderStatusChangedEvent(OrderId orderId, OrderStatus oldStatus, OrderStatus newStatus) {
        this(orderId, oldStatus, newStatus, LocalDateTime.now());
    }
}
