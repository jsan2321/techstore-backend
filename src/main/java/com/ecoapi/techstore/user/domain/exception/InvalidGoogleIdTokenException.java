package com.ecoapi.techstore.user.domain.exception;

/**
 * Exception thrown when a Google ID token cannot be verified.
 */
public class InvalidGoogleIdTokenException extends RuntimeException {

    public InvalidGoogleIdTokenException(String message) {
        super(message);
    }

    public InvalidGoogleIdTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
