package com.ecoapi.techstore.common.domain.valueobjects;

public record PhoneNumber(String value) {
    
    public PhoneNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        // Simple validation: only digits, spaces, dashes, parentheses, and plus sign
        if (!value.matches("[+\\d()\\s-]+")) {
            throw new IllegalArgumentException("Invalid phone number format: " + value);
        }
        value = value.trim();
    }
    
    @Override
    public String toString() {
        return value;
    }
}
