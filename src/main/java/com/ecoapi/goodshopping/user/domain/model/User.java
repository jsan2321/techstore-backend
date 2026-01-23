package com.ecoapi.goodshopping.user.domain.model;

import com.ecoapi.goodshopping.common.domain.valueobjects.Email;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * User Aggregate Root - Pure domain model
 * Contains business logic and invariants
 * No JPA annotations - this is the core business entity
 */
public class User {
    
    private UserId id;
    private String firstName;
    private String lastName;
    private Email email;
    private String passwordHash;
    private boolean active;
    private Set<Role> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Private constructor - use factory methods
    private User(String firstName, String lastName, Email email, String passwordHash) {
        validateFirstName(firstName);
        validateLastName(lastName);
        validatePasswordHash(passwordHash);
        
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.email = email;
        this.passwordHash = passwordHash;
        this.active = true;
        this.roles = new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Private constructor for reconstitution - use factory methods
    private User(UserId id, String firstName, String lastName, Email email, 
                 String passwordHash, boolean active, Set<Role> roles, 
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        validateFirstName(firstName);
        validateLastName(lastName);
        validatePasswordHash(passwordHash);
        
        this.id = id;
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.email = email;
        this.passwordHash = passwordHash;
        this.active = active;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Factory Methods
    
    /**
     * Factory method for user registration
     * Creates a new active user with default roles
     */
    public static User register(String firstName, String lastName, Email email, String passwordHash) {
        return new User(firstName, lastName, email, passwordHash);
    }
    
    /**
     * Factory method for reconstituting user from persistence
     * Used by infrastructure layer to rebuild domain object from database
     */
    public static User reconstitute(UserId id, String firstName, String lastName, Email email,
                                    String passwordHash, boolean active, Set<Role> roles,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new User(id, firstName, lastName, email, passwordHash, active, roles, createdAt, updatedAt);
    }
    
    // Business logic methods
    
    public void updateProfile(String firstName, String lastName) {
        validateFirstName(firstName);
        validateLastName(lastName);
        
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void changePassword(String newPasswordHash) {
        validatePasswordHash(newPasswordHash);
        this.passwordHash = newPasswordHash;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        this.roles.add(role);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void removeRole(Role role) {
        this.roles.remove(role);
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean hasRole(String roleName) {
        return roles.stream()
                .anyMatch(role -> role.getName().equals(roleName.toUpperCase()));
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    // Validation methods
    
    private void validateFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (firstName.length() > 50) {
            throw new IllegalArgumentException("First name cannot exceed 50 characters");
        }
    }
    
    private void validateLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        if (lastName.length() > 50) {
            throw new IllegalArgumentException("Last name cannot exceed 50 characters");
        }
    }
    
    private void validatePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or empty");
        }
    }
    
    // Getters
    
    public UserId getId() {
        return id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public Email getEmail() {
        return email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public Set<Role> getRoles() {
        return new HashSet<>(roles);
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email=" + email +
                ", active=" + active +
                ", roles=" + roles.size() +
                '}';
    }
}
