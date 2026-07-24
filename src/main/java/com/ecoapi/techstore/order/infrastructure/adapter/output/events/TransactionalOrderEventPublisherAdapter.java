package com.ecoapi.techstore.order.infrastructure.adapter.output.events;

import com.ecoapi.techstore.order.application.port.out.OrderEventPublisherPort;
import com.ecoapi.techstore.order.domain.events.OrderCancelledEvent;
import com.ecoapi.techstore.order.domain.events.OrderPlacedEvent;
import com.ecoapi.techstore.order.domain.events.OrderStatusChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Transactional-aware event publisher adapter for Order domain events.
 * 
 * This adapter ensures events are only published AFTER the transaction commits successfully.
 * If the transaction rolls back, events are NOT published, maintaining consistency.
 * 
 * Pattern: Transactional Outbox (simplified version using TransactionSynchronization)
 */
public class TransactionalOrderEventPublisherAdapter implements OrderEventPublisherPort {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionalOrderEventPublisherAdapter.class);
    
    private final ApplicationEventPublisher eventPublisher;
    
    public TransactionalOrderEventPublisherAdapter(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public void publish(OrderPlacedEvent event) {
        publishAfterCommit(event, "OrderPlacedEvent", event.orderId().toString());
    }
    
    @Override
    public void publish(OrderStatusChangedEvent event) {
        String details = event.oldStatus() + " -> " + event.newStatus();
        publishAfterCommit(event, "OrderStatusChangedEvent", event.orderId() + " (" + details + ")");
    }
    
    @Override
    public void publish(OrderCancelledEvent event) {
        publishAfterCommit(event, "OrderCancelledEvent", event.orderId() + " (reason: " + event.reason() + ")");
    }
    
    /**
     * Registers the event to be published after the current transaction commits.
     * If there's no active transaction, publishes immediately (for testing/non-transactional contexts).
     */
    private void publishAfterCommit(Object event, String eventType, String details) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            // Register to publish after commit
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    logger.info("Publishing {} after commit: {}", eventType, details);
                    eventPublisher.publishEvent(event);
                }
                
                @Override
                public void afterCompletion(int status) {
                    if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                        logger.warn("{} NOT published due to transaction rollback: {}", eventType, details);
                    }
                }
            });
            logger.debug("Scheduled {} for after-commit publishing: {}", eventType, details);
        } else {
            // No active transaction - publish immediately (e.g., in tests)
            logger.info("Publishing {} immediately (no active transaction): {}", eventType, details);
            eventPublisher.publishEvent(event);
        }
    }
}
