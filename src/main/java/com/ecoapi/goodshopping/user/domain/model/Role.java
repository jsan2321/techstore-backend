package com.ecoapi.goodshopping.user.domain.model;

import java.util.Objects;

import com.ecoapi.goodshopping.common.domain.valueobjects.RoleId;

/**
 * Role Entity - Pure domain model
 * No JPA annotations - this is the business model
 */
public class Role {
    
    private RoleId id;
    private RoleName name;
    
    // Constructor for creating a new role (without ID)
    public Role(RoleName name) {
        validateName(name);
        this.name = name;
    }
    
    // Constructor for reconstituting from persistence
    public Role(RoleId id, RoleName name) {
        validateName(name);
        this.id = id;
        this.name = name;
    }
    
    private void validateName(RoleName name) {
        if (name == null) throw new IllegalArgumentException("Role name cannot be null or empty");
    }
    
    public RoleId getId() {
        return id;
    }
    
    public RoleName getName() {
        return name;
    }
    
    public void changeName(RoleName newName) {
        validateName(newName);
        this.name = newName;
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
                ", name=" + name +
                '}';
    }
}
