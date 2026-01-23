package com.ecoapi.goodshopping.user.infrastructure.adapter.output.events;

import com.ecoapi.goodshopping.common.domain.events.DomainEvent;
import com.ecoapi.goodshopping.user.application.port.out.UserEventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Adapter that implements UserEventPublisherPort using Spring's ApplicationEventPublisher
 * In a real system, this might publish to Kafka, RabbitMQ, etc.
 */
@Component
public class UserEventPublisherAdapter implements UserEventPublisherPort {
    
    private static final Logger logger = LoggerFactory.getLogger(UserEventPublisherAdapter.class);
    
    private final ApplicationEventPublisher eventPublisher;
    
    public UserEventPublisherAdapter(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public void publish(DomainEvent event) {
        logger.info("Publishing domain event: {}", event.getClass().getSimpleName());
        eventPublisher.publishEvent(event);
    }
}
