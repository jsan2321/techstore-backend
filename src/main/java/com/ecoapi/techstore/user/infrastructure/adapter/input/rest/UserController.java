package com.ecoapi.techstore.user.infrastructure.adapter.input.rest;

import com.ecoapi.techstore.common.domain.valueobjects.PhoneNumber;
import com.ecoapi.techstore.user.application.port.in.ChangePasswordUseCase;
import com.ecoapi.techstore.user.application.port.in.DeactivateUserUseCase;
import com.ecoapi.techstore.user.application.port.in.GetCurrentUserUseCase;
import com.ecoapi.techstore.user.application.port.in.UpdateUserUseCase;
import com.ecoapi.techstore.user.application.service.dto.ChangePasswordCommand;
import com.ecoapi.techstore.user.application.service.dto.DeactivateUserCommand;
import com.ecoapi.techstore.user.application.service.dto.UpdateUserCommand;
import com.ecoapi.techstore.user.domain.model.User;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.common.infrastructure.security.util.SecurityContextUtil;
import com.ecoapi.techstore.user.infrastructure.adapter.input.rest.request.ChangePasswordRequest;
import com.ecoapi.techstore.user.infrastructure.adapter.input.rest.request.UpdateUserRequest;
import com.ecoapi.techstore.user.infrastructure.adapter.input.rest.response.UserResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for User operations (Input Adapter)
 * User-centric operations - all for the authenticated user
 * Admin operations are in AdminUserController
 */
@RestController
@RequestMapping("${api.prefix}/users")
@Tag(name = "Users", description = "User profile management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
        private final DeactivateUserUseCase deactivateUserUseCase;
        private final ChangePasswordUseCase changePasswordUseCase;
    
    public UserController(GetCurrentUserUseCase getCurrentUserUseCase,
                                                  UpdateUserUseCase updateUserUseCase,
                                                  DeactivateUserUseCase deactivateUserUseCase,
                                                  ChangePasswordUseCase changePasswordUseCase) {
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
                this.deactivateUserUseCase = deactivateUserUseCase;
                this.changePasswordUseCase = changePasswordUseCase;
    }
    
    @Operation(
            summary = "Get current user profile",
            description = "Retrieves the profile of the currently authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getCurrentUser() {
        User user = getCurrentUserUseCase.execute();
        return ResponseEntity.ok(UserResponse.from(user));
    }
    
    @Operation(
            summary = "Update current user profile",
            description = "Updates the profile of the currently authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> updateCurrentUser(@Valid @RequestBody UpdateUserRequest request) {
        
        Long currentUserId = SecurityContextUtil.getCurrentUserId()
                .orElseThrow(() -> new SecurityException("No authenticated user"));
        
        PhoneNumber phoneNumber = request.phoneNumber() != null ? new PhoneNumber(request.phoneNumber()) : null;

        UpdateUserCommand command = new UpdateUserCommand(
                currentUserId,
                request.firstName(),
                request.lastName(),
                phoneNumber
        );

        User user = updateUserUseCase.execute(command);
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @Operation(
            summary = "Deactivate my account",
            description = "Deactivates the currently authenticated user's account"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account deactivated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PutMapping("/me/deactivate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> deactivateMyAccount() {

        Long currentUserId = SecurityContextUtil.getCurrentUserId()
                .orElseThrow(() -> new SecurityException("No authenticated user"));

        DeactivateUserCommand command = new DeactivateUserCommand(UserId.of(currentUserId));
        User user = deactivateUserUseCase.execute(command);
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @Operation(
            summary = "Change my password",
            description = "Changes the password for the currently authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changeMyPassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long currentUserId = SecurityContextUtil.getCurrentUserId()
                .orElseThrow(() -> new SecurityException("No authenticated user"));

        ChangePasswordCommand command = new ChangePasswordCommand(
                currentUserId,
                request.currentPassword(),
                request.newPassword(),
                request.confirmPassword()
        );

        changePasswordUseCase.execute(command);
        return ResponseEntity.ok().build();
    }
}
