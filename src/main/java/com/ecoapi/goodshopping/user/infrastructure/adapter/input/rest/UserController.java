package com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest;

import com.ecoapi.goodshopping.user.application.port.in.GetUserProfileUseCase;
import com.ecoapi.goodshopping.user.application.port.in.UpdateUserUseCase;
import com.ecoapi.goodshopping.user.application.service.dto.UpdateUserCommand;
import com.ecoapi.goodshopping.user.domain.model.User;
import com.ecoapi.goodshopping.user.domain.model.UserId;
import com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest.request.UpdateUserRequest;
import com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for User operations (Input Adapter)
 */
@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {
    
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    
    public UserController(GetUserProfileUseCase getUserProfileUseCase, 
                         UpdateUserUseCase updateUserUseCase) {
        this.getUserProfileUseCase = getUserProfileUseCase;
        this.updateUserUseCase = updateUserUseCase;
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable Long userId) {
        User user = getUserProfileUseCase.execute(UserId.of(userId));
        return ResponseEntity.ok(UserResponse.from(user));
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        
        UpdateUserCommand command = new UpdateUserCommand(
                userId,
                request.firstName(),
                request.lastName()
        );
        
        User user = updateUserUseCase.execute(command);
        return ResponseEntity.ok(UserResponse.from(user));
    }
}
