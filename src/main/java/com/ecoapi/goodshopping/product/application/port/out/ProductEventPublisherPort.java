package com.ecoapi.goodshopping.product.application.port.out;

import com.ecoapi.goodshopping.common.domain.events.DomainEvent;

/**
 * Output Port for publishing Product domain events
 * Infrastructure will provide the actual implementation
 */
public interface ProductEventPublisherPort {
    
    void publish(DomainEvent event);
}
