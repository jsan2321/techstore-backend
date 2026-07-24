package com.ecoapi.techstore.cart.infrastructure.adapter.output.persistence;

import com.ecoapi.techstore.cart.application.port.out.CartRepositoryPort;
import com.ecoapi.techstore.cart.domain.model.Cart;
import com.ecoapi.techstore.cart.domain.model.CartId;
import com.ecoapi.techstore.cart.infrastructure.adapter.output.persistence.entity.CartEntity;
import com.ecoapi.techstore.cart.infrastructure.adapter.output.persistence.mapper.CartPersistenceMapper;
import com.ecoapi.techstore.cart.infrastructure.adapter.output.persistence.repository.JpaCartRepository;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;

import java.util.Optional;

public class CartRepositoryAdapter implements CartRepositoryPort {
    
    private final JpaCartRepository jpaCartRepository;
    private final CartPersistenceMapper mapper;
    
    public CartRepositoryAdapter(JpaCartRepository jpaCartRepository,
                                CartPersistenceMapper mapper) {
        this.jpaCartRepository = jpaCartRepository;
        this.mapper = mapper;
    }
    
    @Override
    public Cart save(Cart cart) {
        CartEntity entity = mapper.toEntity(cart);
        CartEntity savedEntity = jpaCartRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Cart> findById(CartId cartId) {
        return jpaCartRepository.findById(cartId.value())
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<Cart> findByUserId(UserId userId) {
        return jpaCartRepository.findByUserId(userId.value())
                .map(mapper::toDomain);
    }
    
    @Override
    public void deleteById(CartId cartId) {
        jpaCartRepository.deleteById(cartId.value());
    }
    
    @Override
    public boolean existsById(CartId cartId) {
        return jpaCartRepository.existsById(cartId.value());
    }
}
