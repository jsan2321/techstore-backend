package com.ecoapi.techstore.user.infrastructure.adapter.input.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * REST request DTO for updating user profile
 * Enhanced to support international addresses with all fields
 * needed for shipping integration.
 */
public record UpdateUserRequest(
    @NotBlank(message = "First name is required")
    @Size(max = 50)
    String firstName,
    
    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    String lastName,
    
    @NotBlank(message = "Phone number is required")
    @Size(max = 15)
    String phoneNumber
) {

}
