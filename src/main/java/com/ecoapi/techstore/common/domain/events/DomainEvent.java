package com.ecoapi.techstore.common.domain.events;

import java.time.LocalDateTime;

/**
 * Base interface for all domain events
 */
public interface DomainEvent {
    LocalDateTime occurredOn();
}
