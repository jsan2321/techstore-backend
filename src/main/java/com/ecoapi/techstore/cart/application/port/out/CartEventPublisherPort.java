package com.ecoapi.techstore.cart.application.port.out;

import com.ecoapi.techstore.common.domain.events.DomainEvent;

/**
 * Port for publishing cart domain events
 * Infrastructure adapter will implement this
 */
public interface CartEventPublisherPort {
    void publish(DomainEvent event);
}
