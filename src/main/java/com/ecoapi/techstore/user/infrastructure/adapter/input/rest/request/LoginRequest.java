package com.ecoapi.techstore.user.infrastructure.adapter.input.rest.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * REST request DTO for login
 */
public record LoginRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,
    
    @NotBlank(message = "Password is required")
    String password
) {
}
