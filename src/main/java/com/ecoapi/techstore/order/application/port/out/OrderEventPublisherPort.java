package com.ecoapi.techstore.order.application.port.out;

import com.ecoapi.techstore.order.domain.events.OrderCancelledEvent;
import com.ecoapi.techstore.order.domain.events.OrderPlacedEvent;
import com.ecoapi.techstore.order.domain.events.OrderStatusChangedEvent;

/**
 * Output port for publishing order domain events
 * Allows the application layer to publish events without knowing the implementation details
 */
public interface OrderEventPublisherPort {
    
    /**
     * Publish an OrderPlacedEvent when a new order is created
     */
    void publish(OrderPlacedEvent event);
    
    /**
     * Publish an OrderStatusChangedEvent when order status changes
     */
    void publish(OrderStatusChangedEvent event);
    
    /**
     * Publish an OrderCancelledEvent when an order is cancelled
     */
    void publish(OrderCancelledEvent event);
}
