package com.ecoapi.techstore.user.infrastructure.adapter.input.rest;

import com.ecoapi.techstore.user.application.port.in.DeleteUserUseCase;
import com.ecoapi.techstore.user.application.port.in.DeactivateUserUseCase;
import com.ecoapi.techstore.user.application.port.in.GetAllUsersUseCase;
import com.ecoapi.techstore.user.application.port.in.GetUserProfileUseCase;
import com.ecoapi.techstore.user.application.port.in.ReactivateUserUseCase;
import com.ecoapi.techstore.user.application.port.in.ListAddressesUseCase;
import com.ecoapi.techstore.user.application.service.dto.DeactivateUserCommand;
import com.ecoapi.techstore.user.application.service.dto.DeleteUserCommand;
import com.ecoapi.techstore.user.application.service.dto.GetAllUsersQuery;
import com.ecoapi.techstore.user.application.service.dto.ReactivateUserCommand;
import com.ecoapi.techstore.user.application.service.dto.ListAddressesQuery;
import com.ecoapi.techstore.user.domain.model.User;
import com.ecoapi.techstore.user.domain.model.UserStatus;
import com.ecoapi.techstore.user.domain.model.SavedAddress;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.user.infrastructure.adapter.input.rest.response.UserResponse;
import com.ecoapi.techstore.user.infrastructure.adapter.input.rest.response.SavedAddressResponse;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Admin User Operations (Input Adapter)
 * Handles user management operations for administrators
 * All operations require ROLE_ADMIN
 */
@RestController
@RequestMapping("${api.prefix}/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Users", description = "User management endpoints for administrators")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private final GetUserProfileUseCase getUserProfileUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final DeactivateUserUseCase deactivateUserUseCase;
    private final ReactivateUserUseCase reactivateUserUseCase;
    private final ListAddressesUseCase listAddressesUseCase;

    public AdminUserController(GetUserProfileUseCase getUserProfileUseCase,
                               GetAllUsersUseCase getAllUsersUseCase,
                               DeleteUserUseCase deleteUserUseCase,
                               DeactivateUserUseCase deactivateUserUseCase,
                               ReactivateUserUseCase reactivateUserUseCase,
                               ListAddressesUseCase listAddressesUseCase) {
        this.getUserProfileUseCase = getUserProfileUseCase;
        this.getAllUsersUseCase = getAllUsersUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.deactivateUserUseCase = deactivateUserUseCase;
        this.reactivateUserUseCase = reactivateUserUseCase;
        this.listAddressesUseCase = listAddressesUseCase;
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves all users with pagination (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status value. Allowed values: ACTIVE, INACTIVE"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Optional user status filter: ACTIVE or INACTIVE") @RequestParam(required = false) UserStatus status) {
        GetAllUsersQuery query = new GetAllUsersQuery(page, size, status);
        List<User> users = getAllUsersUseCase.execute(query);
        List<UserResponse> response = users.stream()
                .map(UserResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }
    
    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a specific user by their ID (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        User user = getUserProfileUseCase.execute(UserId.of(userId));
        return ResponseEntity.ok(UserResponse.from(user));
    }
    
    @Operation(
            summary = "Deactivate user",
            description = "Deactivates a user account (logical deletion) and revokes sessions (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deactivated successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        DeactivateUserCommand command = new DeactivateUserCommand(UserId.of(userId));
        User user = deactivateUserUseCase.execute(command);
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @Operation(
            summary = "Reactivate user",
            description = "Reactivates a previously inactive user account (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User reactivated successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{userId}/reactivate")
    public ResponseEntity<UserResponse> reactivateUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        ReactivateUserCommand command = new ReactivateUserCommand(UserId.of(userId));
        User user = reactivateUserUseCase.execute(command);
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @Operation(
            summary = "Delete user",
            description = "Physically deletes a user from the system. Allowed only when the user has no orders (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "409", description = "Cannot delete user with existing orders"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        DeleteUserCommand command = new DeleteUserCommand(UserId.of(userId));
        deleteUserUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get user addresses",
            description = "Retrieves saved addresses of a user (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Addresses retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userId}/addresses")
    public ResponseEntity<List<SavedAddressResponse>> getUserAddresses(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        List<SavedAddress> addresses = listAddressesUseCase.execute(new ListAddressesQuery(userId));
        List<SavedAddressResponse> response = addresses.stream()
                .map(SavedAddressResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }
}