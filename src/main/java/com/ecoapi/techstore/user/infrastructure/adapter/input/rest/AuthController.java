package com.ecoapi.techstore.user.infrastructure.adapter.input.rest;

import com.ecoapi.techstore.user.application.port.in.LoginUseCase;
import com.ecoapi.techstore.user.application.port.in.LogoutUseCase;
import com.ecoapi.techstore.user.application.port.in.ConfirmEmailUseCase;
import com.ecoapi.techstore.user.application.port.in.GoogleLoginUseCase;
import com.ecoapi.techstore.user.application.port.in.RefreshTokenUseCase;
import com.ecoapi.techstore.user.application.port.in.RegisterUserUseCase;
import com.ecoapi.techstore.user.application.port.in.RequestPasswordResetUseCase;
import com.ecoapi.techstore.user.application.port.in.ResendEmailConfirmationUseCase;
import com.ecoapi.techstore.user.application.port.in.ResetPasswordUseCase;
import com.ecoapi.techstore.user.application.service.dto.ConfirmEmailCommand;
import com.ecoapi.techstore.user.application.service.dto.ForgotPasswordCommand;
import com.ecoapi.techstore.user.application.service.dto.GoogleLoginCommand;
import com.ecoapi.techstore.user.application.service.dto.LoginCommand;
import com.ecoapi.techstore.user.application.service.dto.LogoutCommand;
import com.ecoapi.techstore.user.application.service.dto.RefreshTokenCommand;
import com.ecoapi.techstore.user.application.service.dto.RegisterCommand;
import com.ecoapi.techstore.user.application.service.dto.ResendEmailConfirmationCommand;
import com.ecoapi.techstore.user.application.service.dto.ResetPasswordCommand;
import com.ecoapi.techstore.user.domain.model.AuthenticationResult;
import com.ecoapi.techstore.user.domain.model.User;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.common.infrastructure.security.util.SecurityContextUtil;
import com.ecoapi.techstore.user.infrastructure.adapter.input.rest.request.ForgotPasswordRequest;
import com.ecoapi.techstore.user.infrastructure.adapter.input.rest.request.GoogleLoginRequest;
import com.ecoapi.techstore.user.infrastructure.adapter.input.rest.request.LoginRequest;
import com.ecoapi.techstore.user.infrastructure.adapter.input.rest.request.RegisterRequest;
import com.ecoapi.techstore.user.infrastructure.adapter.input.rest.request.ResendEmailConfirmationRequest;
import com.ecoapi.techstore.user.infrastructure.adapter.input.rest.request.ResetPasswordRequest;
import com.ecoapi.techstore.user.infrastructure.adapter.input.rest.response.AuthResponse;
import com.ecoapi.techstore.user.infrastructure.adapter.input.rest.response.UserResponse;

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
import org.springframework.http.ResponseCookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Authentication (Input Adapter)
 * Maps HTTP requests to Use Cases
 */
@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and authorization endpoints")
public class AuthController {
    
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final GoogleLoginUseCase googleLoginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final ConfirmEmailUseCase confirmEmailUseCase;
    private final ResendEmailConfirmationUseCase resendEmailConfirmationUseCase;
    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;

    @Value("${app.security.refresh-cookie.name}")
    private String refreshCookieName;

    @Value("${app.security.refresh-cookie.secure}")
    private boolean refreshCookieSecure;

    @Value("${app.security.refresh-cookie.same-site}")
    private String refreshCookieSameSite;

    @Value("${app.security.refresh-cookie.path}")
    private String refreshCookiePath;

    @Value("${auth.refreshToken.expirationInDays}")
    private long refreshTokenExpirationDays;
    
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided details"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
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
    
    @Operation(
            summary = "User login",
            description = "Authenticates user credentials and returns JWT tokens"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
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
        return authenticatedResponse(result);
    }

    @Operation(
            summary = "Google login",
            description = "Authenticates a Google ID token and returns JWT tokens"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid Google token")
    })
    @PostMapping("/google-login")
    public ResponseEntity<AuthResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        AuthenticationResult result = googleLoginUseCase.execute(new GoogleLoginCommand(request.idToken()));
        return authenticatedResponse(result);
    }

    @Operation(
            summary = "Logout user",
            description = "Invalidates the user's refresh token to end the session"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(@CookieValue(name = "techstore_refresh", required = false) String refreshToken) {
                                        
        // Get user ID from security context
        UserId userId = SecurityContextUtil.getCurrentUserIdAsDomain()
                .orElseThrow(() -> new IllegalStateException("User not authenticated"));
        
        // Create logout command
        LogoutCommand command = new LogoutCommand(
                userId,
                refreshToken
        );
        
        logoutUseCase.logout(command);
        return ResponseEntity.ok()
                .header("Set-Cookie", expiredRefreshCookie().toString())
                .build();
    }
    
    @Operation(
            summary = "Refresh access token",
            description = "Get a new access token using a valid refresh token. This allows users to stay logged in without re-entering credentials."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token successfully refreshed",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @RequestMapping(value = "/refresh", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "techstore_refresh", required = false) String refreshToken) {
        RefreshTokenCommand command = new RefreshTokenCommand(refreshToken);
        AuthenticationResult result = refreshTokenUseCase.refreshAccessToken(command);
        return authenticatedResponse(result);
    }

    @Operation(
            summary = "Confirm email",
            description = "Confirms a pending account using a verification token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email confirmed successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token")
    })
    @PostMapping("/confirm-email")
    public ResponseEntity<UserResponse> confirmEmail(@RequestParam("token") String token) {
        User user = confirmEmailUseCase.execute(new ConfirmEmailCommand(token));
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @Operation(
            summary = "Resend confirmation email",
            description = "Resends a confirmation email for pending accounts"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Request accepted")
    })
    @PostMapping("/resend-confirmation")
    public ResponseEntity<Void> resendConfirmation(
            @Valid @RequestBody ResendEmailConfirmationRequest request) {
        resendEmailConfirmationUseCase.execute(new ResendEmailConfirmationCommand(request.email()));
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Forgot password",
            description = "Starts password reset flow. Always returns accepted to avoid account enumeration"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Request accepted")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        requestPasswordResetUseCase.execute(new ForgotPasswordCommand(request.email()));
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Reset password",
            description = "Resets user password using a valid reset token"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or token")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        resetPasswordUseCase.execute(new ResetPasswordCommand(
                request.token(),
                request.newPassword(),
                request.confirmPassword()
        ));
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<AuthResponse> authenticatedResponse(AuthenticationResult result) {
        if (result.refreshToken() == null) {
            throw new IllegalStateException("Authentication did not return a refresh token");
        }
        return ResponseEntity.ok()
                .header("Set-Cookie", refreshCookie(result.refreshToken().getToken()).toString())
                .body(AuthResponse.from(result));
    }

    private ResponseCookie refreshCookie(String token) {
        return ResponseCookie.from(refreshCookieName, token)
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .sameSite(refreshCookieSameSite)
                .path(refreshCookiePath)
                .maxAge(java.time.Duration.ofDays(refreshTokenExpirationDays))
                .build();
    }

    private ResponseCookie expiredRefreshCookie() {
        return ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .sameSite(refreshCookieSameSite)
                .path(refreshCookiePath)
                .maxAge(0)
                .build();
    }
}
