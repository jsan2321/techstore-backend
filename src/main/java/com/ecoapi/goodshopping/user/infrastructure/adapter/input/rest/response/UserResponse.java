package com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest.response;

import com.ecoapi.goodshopping.user.domain.model.User;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST response DTO for User
 */
public record UserResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    boolean active,
    Set<String> roles
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId() != null ? user.getId().value() : null,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail().value(),
                user.isActive(),
                user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet())
        );
    }
}
