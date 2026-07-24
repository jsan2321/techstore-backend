package com.ecoapi.techstore.user.infrastructure.adapter.input.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * REST request DTO for resetting password with one-time token.
 */
public record ResetPasswordRequest(
        @NotBlank(message = "Token is required")
        String token,

        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String newPassword,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword
) {
}
