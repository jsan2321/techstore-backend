package com.ecoapi.techstore.common.domain.valueobjects;

import java.util.regex.Pattern;

/**
 * Value Object representing an email address
 * Immutable and self-validating
 */
public record Email(String value) {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
        value = value.toLowerCase().trim();
    }
    
    @Override
    public String toString() {
        return value;
    }
}
