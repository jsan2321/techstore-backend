package com.ecoapi.techstore.user.application.port.out;

import com.ecoapi.techstore.common.domain.events.DomainEvent;

/**
 * Output Port for publishing domain events
 */
public interface UserEventPublisherPort {
    
    void publish(DomainEvent event);
}
