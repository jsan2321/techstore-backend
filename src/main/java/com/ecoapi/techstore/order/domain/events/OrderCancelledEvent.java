package com.ecoapi.techstore.order.domain.events;

import com.ecoapi.techstore.common.domain.events.DomainEvent;
import com.ecoapi.techstore.order.domain.model.OrderId;

import java.time.LocalDateTime;

public record OrderCancelledEvent(
    OrderId orderId,
    Long userId,
    String reason,
    LocalDateTime occurredOn
) implements DomainEvent {
    
    public OrderCancelledEvent(OrderId orderId, Long userId, String reason) {
        this(orderId, userId, reason, LocalDateTime.now());
    }
}
