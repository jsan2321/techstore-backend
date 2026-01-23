package com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest;

import com.ecoapi.goodshopping.user.application.port.in.LoginUseCase;
import com.ecoapi.goodshopping.user.application.port.in.RegisterUserUseCase;
import com.ecoapi.goodshopping.user.application.service.dto.AuthenticationResult;
import com.ecoapi.goodshopping.user.application.service.dto.LoginCommand;
import com.ecoapi.goodshopping.user.application.service.dto.RegisterCommand;
import com.ecoapi.goodshopping.user.domain.model.User;
import com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest.request.LoginRequest;
import com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest.request.RegisterRequest;
import com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest.response.AuthResponse;
import com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Authentication (Input Adapter)
 * Maps HTTP requests to Use Cases
 */
@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {
    
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    
    public AuthController(RegisterUserUseCase registerUserUseCase, LoginUseCase loginUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
    }
    
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        // Map REST request to Command
        RegisterCommand command = new RegisterCommand(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password()
        );
        
        // Execute use case
        User user = registerUserUseCase.execute(command);
        
        // Map domain model to REST response
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponse.from(user));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // Map REST request to Command
        LoginCommand command = new LoginCommand(
                request.email(),
                request.password()
        );
        
        // Execute use case
        AuthenticationResult result = loginUseCase.execute(command);
        
        // Map to REST response
        AuthResponse response = new AuthResponse(
                result.user().getId().value(),
                result.token()
        );
        
        return ResponseEntity.ok(response);
    }
}
