package com.ecoapi.techstore.common.infrastructure.exception;

import com.ecoapi.techstore.common.domain.exceptions.DomainException;
import com.ecoapi.techstore.common.domain.exceptions.InsufficientStockException;
import com.ecoapi.techstore.common.domain.exceptions.NotFoundException;
import com.ecoapi.techstore.common.domain.exceptions.ValidationException;
import com.ecoapi.techstore.user.domain.exception.EmailAlreadyExistsException;
import com.ecoapi.techstore.user.domain.exception.AddressNotFoundException;
import com.ecoapi.techstore.user.domain.exception.EmailNotVerifiedException;
import com.ecoapi.techstore.user.domain.exception.InvalidCurrentPasswordException;
import com.ecoapi.techstore.user.domain.exception.InvalidCredentialsException;
import com.ecoapi.techstore.user.domain.exception.InvalidEmailVerificationTokenException;
import com.ecoapi.techstore.user.domain.exception.InvalidGoogleIdTokenException;
import com.ecoapi.techstore.user.domain.exception.InvalidPasswordResetTokenException;
import com.ecoapi.techstore.user.domain.exception.InvalidRefreshTokenException;
import com.ecoapi.techstore.user.domain.exception.UserHasOrdersException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for all REST controllers
 * Handles both domain exceptions and infrastructure exceptions
 * Returns consistent error responses across the API
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handle NotFoundException (404)
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            NotFoundException ex, 
            HttpServletRequest request) {
        logger.warn("Resource not found: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle AddressNotFoundException (404)
     */
    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAddressNotFoundException(
            AddressNotFoundException ex,
            HttpServletRequest request) {
        logger.warn("Address not found: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Handle ValidationException (400)
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex, 
            HttpServletRequest request) {
        logger.warn("Validation error: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(error);
    }
    
    /**
     * Handle EmailAlreadyExistsException (409 Conflict)
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException ex, 
            HttpServletRequest request) {
        logger.warn("Email already exists: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Conflict",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    /**
     * Handle InsufficientStockException (409 Conflict)
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStockException(
            InsufficientStockException ex, 
            HttpServletRequest request) {
        logger.warn("Insufficient stock: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Insufficient Stock",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handle UserHasOrdersException (409 Conflict)
     */
    @ExceptionHandler(UserHasOrdersException.class)
    public ResponseEntity<ErrorResponse> handleUserHasOrdersException(
            UserHasOrdersException ex,
            HttpServletRequest request) {
        logger.warn("User deletion conflict: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Conflict",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    /**
     * Handle InvalidCredentialsException (401 Unauthorized)
     */
    @ExceptionHandler({InvalidCredentialsException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
            Exception ex, 
            HttpServletRequest request) {
        logger.warn("Invalid credentials attempt from: {}", request.getRemoteAddr());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Unauthorized",
            "Invalid email or password",
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handle InvalidGoogleIdTokenException (401 Unauthorized)
     */
    @ExceptionHandler(InvalidGoogleIdTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidGoogleIdTokenException(
            InvalidGoogleIdTokenException ex,
            HttpServletRequest request) {
        logger.warn("Invalid Google token: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handle EmailNotVerifiedException (403 Forbidden)
     */
    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleEmailNotVerifiedException(
            EmailNotVerifiedException ex,
            HttpServletRequest request) {
        logger.warn("Email not verified: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    /**
     * Handle InvalidRefreshTokenException (401 Unauthorized)
     */
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshTokenException(
            InvalidRefreshTokenException ex, 
            HttpServletRequest request) {
        logger.warn("Invalid refresh token: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Unauthorized",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handle email verification token errors (400)
     */
    @ExceptionHandler(InvalidEmailVerificationTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEmailVerificationTokenException(
            InvalidEmailVerificationTokenException ex,
            HttpServletRequest request) {
        logger.warn("Invalid email verification token: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle password reset token errors (400)
     */
    @ExceptionHandler(InvalidPasswordResetTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPasswordResetTokenException(
            InvalidPasswordResetTokenException ex,
            HttpServletRequest request) {
        logger.warn("Invalid password reset token: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle InvalidCurrentPasswordException (400)
     */
    @ExceptionHandler(InvalidCurrentPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCurrentPasswordException(
            InvalidCurrentPasswordException ex,
            HttpServletRequest request) {
        logger.warn("Invalid current password: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(error);
    }
    
    /**
     * Handle Spring Security AuthenticationException (401)
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, 
            HttpServletRequest request) {
        logger.warn("Authentication failed: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Unauthorized",
            "Authentication failed: " + ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    /**
     * Handle Spring Security AccessDeniedException (403)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, 
            HttpServletRequest request) {
        logger.warn("Access denied for path: {}", request.getRequestURI());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "Forbidden",
            "Access denied. You don't have permission to access this resource.",
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    /**
     * Handle Bean Validation exceptions (400)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, 
            HttpServletRequest request) {
        logger.warn("Validation failed: {}", ex.getMessage());
        
        List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();
        
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.add(new ErrorResponse.ValidationError(
                fieldError.getField(),
                fieldError.getRejectedValue() != null ? fieldError.getRejectedValue().toString() : "null",
                fieldError.getDefaultMessage()
            ));
        }
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            "Input validation failed",
            request.getRequestURI(),
            validationErrors
        );
        
        return ResponseEntity.badRequest().body(error);
    }
    
    /**
     * Handle IllegalArgumentException (400)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, 
            HttpServletRequest request) {
        logger.warn("Illegal argument: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(error);
    }
    
    /**
     * Handle generic DomainException (400)
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(
            DomainException ex, 
            HttpServletRequest request) {
        logger.warn("Domain exception: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Domain Error",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(error);
    }
    
    /**
     * Handle all other exceptions (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, 
            HttpServletRequest request) {
        logger.error("Unexpected error occurred", ex);
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
