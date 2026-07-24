package com.ecoapi.techstore.order.domain.model;

public enum OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED;
    
    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == PROCESSING || newStatus == CANCELLED;
            case PROCESSING -> newStatus == SHIPPED || newStatus == CANCELLED;
            case SHIPPED -> newStatus == DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }
    
    public boolean isFinal() {
        return this == DELIVERED || this == CANCELLED;
    }
    
    public boolean canBeCancelled() {
        return this == PENDING || this == PROCESSING;
    }
}
