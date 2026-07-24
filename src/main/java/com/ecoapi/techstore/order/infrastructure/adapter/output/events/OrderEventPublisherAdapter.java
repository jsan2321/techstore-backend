package com.ecoapi.techstore.order.infrastructure.adapter.output.events;

import com.ecoapi.techstore.order.application.port.out.OrderEventPublisherPort;
import com.ecoapi.techstore.order.domain.events.OrderCancelledEvent;
import com.ecoapi.techstore.order.domain.events.OrderPlacedEvent;
import com.ecoapi.techstore.order.domain.events.OrderStatusChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Infrastructure adapter for publishing order domain events
 * Implements the OrderEventPublisherPort using Spring's ApplicationEventPublisher
 */
public class OrderEventPublisherAdapter implements OrderEventPublisherPort {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderEventPublisherAdapter.class);
    
    private final ApplicationEventPublisher eventPublisher;
    
    public OrderEventPublisherAdapter(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public void publish(OrderPlacedEvent event) {
        logger.info("Publishing OrderPlacedEvent for order: {}", event.orderId());
        eventPublisher.publishEvent(event);
    }
    
    @Override
    public void publish(OrderStatusChangedEvent event) {
        logger.info("Publishing OrderStatusChangedEvent: {} -> {} for order: {}", 
                event.oldStatus(), event.newStatus(), event.orderId());
        eventPublisher.publishEvent(event);
    }
    
    @Override
    public void publish(OrderCancelledEvent event) {
        logger.info("Publishing OrderCancelledEvent for order: {}, reason: {}", 
                event.orderId(), event.reason());
        eventPublisher.publishEvent(event);
    }
}
