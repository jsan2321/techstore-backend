package com.ecoapi.techstore.common.domain.valueobjects;

/**
 * Value Object representing a Role's unique identifier
 * 
 * Part of the Shared Kernel - can be reused across contexts
 */
public record RoleId(Long value) {
    
    public RoleId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("RoleId must be a positive number");
        }
    }
    
    public static RoleId of(Long value) {
        return new RoleId(value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
