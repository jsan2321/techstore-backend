package com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA Entity for Role persistence
 */
@Entity
@Getter
@Setter
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
    
}
