package com.ecoapi.techstore.user.infrastructure.adapter.input.rest.response;

import com.ecoapi.techstore.user.domain.model.User;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST response DTO for User
 * Includes full profile information including address and phone for frontend use
 */
public record UserResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    Set<String> roles,
    String status,
    boolean emailVerified
) {
    public static UserResponse from(User user) {
        
        return new UserResponse(
                user.getId() != null ? user.getId().value() : null,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail().value(),
                user.getPhoneNumber() != null ? user.getPhoneNumber().value() : null,
                user.getRoles().stream()
                        .map(role -> role.getName().name())
                    .collect(Collectors.toSet()),
                user.getStatus().name(),
                user.isEmailVerified()
        );
    }
    
}
