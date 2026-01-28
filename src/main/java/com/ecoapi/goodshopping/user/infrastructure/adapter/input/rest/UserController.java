package com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest;

import com.ecoapi.goodshopping.common.domain.valueobjects.Address;
import com.ecoapi.goodshopping.common.domain.valueobjects.PhoneNumber;
import com.ecoapi.goodshopping.user.application.port.in.DeleteUserUseCase;
import com.ecoapi.goodshopping.user.application.port.in.GetAllUsersQuery;
import com.ecoapi.goodshopping.user.application.port.in.GetAllUsersUseCase;
import com.ecoapi.goodshopping.user.application.port.in.GetCurrentUserUseCase;
import com.ecoapi.goodshopping.user.application.port.in.GetUserProfileUseCase;
import com.ecoapi.goodshopping.user.application.port.in.UpdateUserUseCase;
import com.ecoapi.goodshopping.user.application.service.dto.DeleteUserCommand;
import com.ecoapi.goodshopping.user.application.service.dto.UpdateUserCommand;
import com.ecoapi.goodshopping.user.domain.model.User;
import com.ecoapi.goodshopping.common.domain.valueobjects.UserId;
import com.ecoapi.goodshopping.common.infrastructure.security.util.SecurityContextUtil;
import com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest.request.UpdateUserRequest;
import com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest.response.UserResponse;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for User operations (Input Adapter)
 */
@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {
    
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    
    public UserController(GetUserProfileUseCase getUserProfileUseCase,
                         GetCurrentUserUseCase getCurrentUserUseCase,
                         UpdateUserUseCase updateUserUseCase,
                         GetAllUsersUseCase getAllUsersUseCase,
                         DeleteUserUseCase deleteUserUseCase) {
        this.getUserProfileUseCase = getUserProfileUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.getAllUsersUseCase = getAllUsersUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
    }
    
    /**
     * Get the profile of the currently authenticated user
     * This endpoint uses the JWT token to identify the user (no ID in URL needed)
     * The user ID is extracted from the SecurityContext (from the JWT token)
     * The convenient endpoint for the Frontend
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getCurrentUser() {
        User user = getCurrentUserUseCase.execute();
        return ResponseEntity.ok(UserResponse.from(user));
    }

    // The specific endpoint for Admins (or the user themselves)
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable Long userId) {
        User user = getUserProfileUseCase.execute(UserId.of(userId));
        return ResponseEntity.ok(UserResponse.from(user));
    }
    
    /**
     * Update user profile
     * Users can only update their own profile (enforced at service layer)
     */
   @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> updateCurrentUser(@Valid @RequestBody UpdateUserRequest request) {
        
        Long currentUserId = SecurityContextUtil.getCurrentUserId()
                .orElseThrow(() -> new SecurityException("No authenticated user"));
        
        return processUpdate(currentUserId, request);
    }
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.userId")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId, @Valid @RequestBody UpdateUserRequest request) {
        
        return processUpdate(userId, request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        
        GetAllUsersQuery query = new GetAllUsersQuery(page, size);
        List<User> users = getAllUsersUseCase.execute(query);
        List<UserResponse> responses = users.stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        DeleteUserCommand command = new DeleteUserCommand(UserId.of(userId));
        deleteUserUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    // Helper method to keep code DRY (Don't Repeat Yourself)
    private ResponseEntity<UserResponse> processUpdate(Long userId, UpdateUserRequest request) {
        PhoneNumber phoneNumber = request.phoneNumber() != null ? new PhoneNumber(request.phoneNumber()) : null;
        Address address = request.address() != null ? 
                new Address(request.address().street(), request.address().city(), request.address().zipCode()) : null;

        UpdateUserCommand command = new UpdateUserCommand(
                userId,
                request.firstName(),
                request.lastName(),
                phoneNumber,
                address
        );

        User user = updateUserUseCase.execute(command);
        return ResponseEntity.ok(UserResponse.from(user));
    }
}
