package com.ecoapi.goodshopping.user.domain.model;

import java.util.Objects;

/**
 * Role Entity - Pure domain model
 * No JPA annotations - this is the business model
 */
public class Role {
    
    private RoleId id;
    private String name;
    
    // Constructor for creating a new role (without ID)
    public Role(String name) {
        validateName(name);
        this.name = name.toUpperCase().trim();
    }
    
    // Constructor for reconstituting from persistence
    public Role(RoleId id, String name) {
        validateName(name);
        this.id = id;
        this.name = name.toUpperCase().trim();
    }
    
    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }
    }
    
    public RoleId getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void changeName(String newName) {
        validateName(newName);
        this.name = newName.toUpperCase().trim();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
