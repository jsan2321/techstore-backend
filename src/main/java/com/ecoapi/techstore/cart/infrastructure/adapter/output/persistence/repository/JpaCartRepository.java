package com.ecoapi.techstore.cart.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.techstore.cart.infrastructure.adapter.output.persistence.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaCartRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
