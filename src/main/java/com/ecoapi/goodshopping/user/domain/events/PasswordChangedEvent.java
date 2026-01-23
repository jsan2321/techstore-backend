package com.ecoapi.goodshopping.user.domain.events;

import com.ecoapi.goodshopping.common.domain.events.DomainEvent;
import com.ecoapi.goodshopping.user.domain.model.UserId;

import java.time.LocalDateTime;

/**
 * Domain event fired when a user changes their password
 */
public record PasswordChangedEvent(
    UserId userId,
    LocalDateTime occurredOn
) implements DomainEvent {
    
    public PasswordChangedEvent(UserId userId) {
        this(userId, LocalDateTime.now());
    }
}
