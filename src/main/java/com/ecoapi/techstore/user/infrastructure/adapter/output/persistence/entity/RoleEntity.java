package com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.entity;

import com.ecoapi.techstore.user.domain.model.RoleName;

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
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private RoleName name;
    
    // Default constructor for JPA
    public RoleEntity() {
    }
    
    public RoleEntity(RoleName name) {
        this.name = name;
    }
    
}
