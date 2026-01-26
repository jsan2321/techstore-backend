package com.ecoapi.goodshopping.user.domain.events;

import com.ecoapi.goodshopping.common.domain.events.DomainEvent;
import com.ecoapi.goodshopping.common.domain.valueobjects.UserId;

import java.time.LocalDateTime;

/**
 * Domain event fired when a new user is registered
 */
public record UserRegisteredEvent(
    UserId userId,
    String email,
    String firstName,
    String lastName,
    LocalDateTime occurredOn
) implements DomainEvent {
    
    public UserRegisteredEvent(UserId userId, String email, String firstName, String lastName) {
        this(userId, email, firstName, lastName, LocalDateTime.now());
    }
}
