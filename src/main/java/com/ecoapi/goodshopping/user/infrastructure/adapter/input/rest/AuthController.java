package com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest;

import com.ecoapi.goodshopping.user.application.port.in.LoginUseCase;
import com.ecoapi.goodshopping.user.application.port.in.LogoutUseCase;
import com.ecoapi.goodshopping.user.application.port.in.RefreshTokenUseCase;
import com.ecoapi.goodshopping.user.application.port.in.RegisterUserUseCase;
import com.ecoapi.goodshopping.user.application.service.dto.LoginCommand;
import com.ecoapi.goodshopping.user.application.service.dto.LogoutCommand;
import com.ecoapi.goodshopping.user.application.service.dto.RefreshTokenCommand;
import com.ecoapi.goodshopping.user.application.service.dto.RegisterCommand;
import com.ecoapi.goodshopping.user.domain.model.AuthenticationResult;
import com.ecoapi.goodshopping.user.domain.model.User;
import com.ecoapi.goodshopping.common.domain.valueobjects.UserId;
import com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest.request.LoginRequest;
import com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest.request.LogoutRequest;
import com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest.request.RefreshTokenRequest;
import com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest.request.RegisterRequest;
import com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest.response.AuthResponse;
import com.ecoapi.goodshopping.user.infrastructure.adapter.input.rest.response.UserResponse;
import com.ecoapi.goodshopping.user.infrastructure.security.util.SecurityContextUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Authentication (Input Adapter)
 * Maps HTTP requests to Use Cases
 */
@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    
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
        
        // Map to REST response using the static from() method
        return ResponseEntity.ok(AuthResponse.from(result));
    }


    /**
     * POST /api/v1/auth/logout
     * Logout the current user by invalidating their refresh token and blacklisting the access token
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request, 
                                       @RequestHeader(value = "Authorization", required = false) String authHeader) {
        // Extract access token from Authorization header
        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }
        
        // Get user ID from security context
        UserId userId = SecurityContextUtil.getCurrentUserIdAsDomain()
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));
        
        // Create logout command
        LogoutCommand command = new LogoutCommand(
                userId,
                accessToken
                //request.refreshToken()
        );
        
        logoutUseCase.logout(command);
        return ResponseEntity.ok().build();
    }
    
    /**
     * POST /api/v1/auth/refresh
     * Get a new access token using a valid refresh token
     * This allows users to stay logged in without re-entering credentials
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenCommand command = new RefreshTokenCommand(request.refreshToken());
        AuthenticationResult result = refreshTokenUseCase.refreshAccessToken(command);
        return ResponseEntity.ok(AuthResponse.from(result));
    }
}
