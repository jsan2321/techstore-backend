package com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    String lastName,
    
    @NotBlank(message = "Phone number is required")
    @Size(max = 15)
    String phoneNumber,
    
    @NotNull(message = "Address object cannot be null")
    @Valid
    AddressRequest address
) {
    public record AddressRequest(
        @NotBlank(message = "Street is required") // Changed to NotBlank for safety
        @Size(max = 100)
        String street,
        
        @NotBlank(message = "City is required")
        @Size(max = 50)
        String city,
        
        @NotBlank(message = "Zip code is required")
        @Size(max = 10)
        String zipCode
    ) {}
}
