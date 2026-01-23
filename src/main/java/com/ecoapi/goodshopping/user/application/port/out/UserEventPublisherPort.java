package com.ecoapi.goodshopping.user.application.port.out;

import com.ecoapi.goodshopping.common.domain.events.DomainEvent;

/**
 * Output Port for publishing domain events
 */
public interface UserEventPublisherPort {
    
    void publish(DomainEvent event);
}
