package com.ecoapi.techstore.order.application.service.dto;

import com.ecoapi.techstore.order.domain.model.OrderStatus;

/**
 * Command for updating order status.
 * Encapsulates all information needed to change an order's status.
 * 
 * Business defaults (like default cancellation reason) are applied in factory methods,
 * keeping the DTO layer pure and moving business rules to the application layer.
 */
public record UpdateOrderStatusCommand(
    Long orderId,
    OrderStatus targetStatus,
    String reason
) {
    /** Default reason when a customer cancels without providing one */
    public static final String DEFAULT_CANCELLATION_REASON = "Customer requested cancellation";
    
    public UpdateOrderStatusCommand {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (targetStatus == null) {
            throw new IllegalArgumentException("Target status cannot be null");
        }
    }
    
    /**
     * Create command to process an order
     */
    public static UpdateOrderStatusCommand process(Long orderId) {
        return new UpdateOrderStatusCommand(orderId, OrderStatus.PROCESSING, null);
    }
    
    /**
     * Create command to ship an order
     */
    public static UpdateOrderStatusCommand ship(Long orderId) {
        return new UpdateOrderStatusCommand(orderId, OrderStatus.SHIPPED, null);
    }
    
    /**
     * Create command to deliver an order
     */
    public static UpdateOrderStatusCommand deliver(Long orderId) {
        return new UpdateOrderStatusCommand(orderId, OrderStatus.DELIVERED, null);
    }
    
    /**
     * Create command to cancel an order.
     * If no reason is provided, a default reason is applied (business rule).
     */
    public static UpdateOrderStatusCommand cancel(Long orderId, String reason) {
        String effectiveReason = (reason != null && !reason.isBlank()) 
            ? reason 
            : DEFAULT_CANCELLATION_REASON;
        return new UpdateOrderStatusCommand(orderId, OrderStatus.CANCELLED, effectiveReason);
    }
}
