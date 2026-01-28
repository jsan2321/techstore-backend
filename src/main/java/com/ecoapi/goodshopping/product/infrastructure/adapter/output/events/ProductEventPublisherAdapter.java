package com.ecoapi.goodshopping.product.infrastructure.adapter.output.events;

import com.ecoapi.goodshopping.common.domain.events.DomainEvent;
import com.ecoapi.goodshopping.product.application.port.out.ProductEventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Adapter for publishing Product domain events to Spring's event system
 * Implements ProductEventPublisherPort from application layer
 */
public class ProductEventPublisherAdapter implements ProductEventPublisherPort {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductEventPublisherAdapter.class);
    
    private final ApplicationEventPublisher eventPublisher;
    
    public ProductEventPublisherAdapter(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public void publish(DomainEvent event) {
        logger.info("Publishing domain event: {}", event.getClass().getSimpleName());
        eventPublisher.publishEvent(event);
    }
}
