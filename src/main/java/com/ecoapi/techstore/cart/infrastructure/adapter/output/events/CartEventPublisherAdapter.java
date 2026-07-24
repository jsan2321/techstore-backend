package com.ecoapi.techstore.cart.infrastructure.adapter.output.events;

import com.ecoapi.techstore.cart.application.port.out.CartEventPublisherPort;
import com.ecoapi.techstore.common.domain.events.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Infrastructure adapter for publishing cart domain events
 * Implements the CartEventPublisherPort from the application layer
 */
public class CartEventPublisherAdapter implements CartEventPublisherPort {
    
    private static final Logger logger = LoggerFactory.getLogger(CartEventPublisherAdapter.class);
    
    private final ApplicationEventPublisher eventPublisher;
    
    public CartEventPublisherAdapter(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public void publish(DomainEvent event) {
        logger.info("Publishing domain event: {}", event.getClass().getSimpleName());
        eventPublisher.publishEvent(event);
    }
}
