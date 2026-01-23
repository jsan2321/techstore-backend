package com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * REST request DTO for updating user profile
 */
public record UpdateUserRequest(
    @NotBlank(message = "First name is required")
    @Size(max = 50)
    String firstName,
    
    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    String lastName
) {
}
