package com.ecoapi.techstore.order.domain.events;

import com.ecoapi.techstore.common.domain.events.DomainEvent;
import com.ecoapi.techstore.order.domain.model.OrderId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderPlacedEvent(
    OrderId orderId,
    Long userId,
    BigDecimal totalAmount,
    int totalItems,
    LocalDateTime occurredOn
) implements DomainEvent {
    
    public OrderPlacedEvent(OrderId orderId, Long userId, BigDecimal totalAmount, int totalItems) {
        this(orderId, userId, totalAmount, totalItems, LocalDateTime.now());
    }
}
