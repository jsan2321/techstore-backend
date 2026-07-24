package com.ecoapi.techstore.user.infrastructure.adapter.input.rest;

import com.ecoapi.techstore.user.application.port.in.LoginUseCase;
import com.ecoapi.techstore.user.application.service.dto.LoginCommand;
import com.ecoapi.techstore.user.domain.model.AuthenticationResult;
import com.ecoapi.techstore.user.domain.model.RoleName;
import com.ecoapi.techstore.user.infrastructure.adapter.input.rest.request.LoginRequest;
import com.ecoapi.techstore.user.infrastructure.adapter.input.rest.response.AuthResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for Admin Authentication (Input Adapter)
 * Provides a dedicated, isolated login endpoint for administrators only.
 * Regular users receive 403 Forbidden even with valid credentials.
 * This endpoint is publicly accessible (no @PreAuthorize) — credentials + role check are the security.
 */
@RestController
@RequestMapping("${api.prefix}/admin/auth")
@RequiredArgsConstructor
@Tag(name = "Admin - Authentication", description = "Dedicated admin login endpoint")
public class AdminAuthController {

    private final LoginUseCase loginUseCase;

    @Operation(
            summary = "Admin login",
            description = "Authenticates credentials and returns JWT tokens. Returns 403 if the user is not an administrator."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin authenticated successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "403", description = "User is not an administrator")
    })
    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(request.email(), request.password());
        AuthenticationResult result = loginUseCase.execute(command);

        boolean isAdmin = result.user().getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied. Admin credentials required."));
        }

        return ResponseEntity.ok(AuthResponse.from(result));
    }
}
