package com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.*;

/**
 * JPA Entity for Role persistence
 */
@Entity
@Table(name = "roles")
public class RoleEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String name;
    
    // Default constructor for JPA
    public RoleEntity() {
    }
    
    public RoleEntity(String name) {
        this.name = name;
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
