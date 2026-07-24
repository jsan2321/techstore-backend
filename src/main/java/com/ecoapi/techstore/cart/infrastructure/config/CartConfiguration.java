package com.ecoapi.techstore.cart.infrastructure.config;

import com.ecoapi.techstore.cart.application.port.in.*;
import com.ecoapi.techstore.cart.application.port.out.CartEventPublisherPort;
import com.ecoapi.techstore.cart.application.port.out.CartRepositoryPort;
import com.ecoapi.techstore.cart.application.port.out.ProductAccessPort;
import com.ecoapi.techstore.cart.application.service.*;
import com.ecoapi.techstore.cart.infrastructure.adapter.TransactionalUseCaseWrapper;
import com.ecoapi.techstore.cart.infrastructure.adapter.output.events.CartEventPublisherAdapter;
import com.ecoapi.techstore.cart.infrastructure.adapter.output.persistence.CartRepositoryAdapter;
import com.ecoapi.techstore.cart.infrastructure.adapter.output.persistence.mapper.CartPersistenceMapper;
import com.ecoapi.techstore.cart.infrastructure.adapter.output.persistence.repository.JpaCartRepository;
import com.ecoapi.techstore.cart.infrastructure.adapter.output.product.ProductAccessAdapter;
import com.ecoapi.techstore.product.application.port.in.GetProductByIdUseCase;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Configuration for Cart Bounded Context
 * Wires all infrastructure adapters and application services
 * This is the ONLY place where framework-specific wiring happens
 * Application layer remains framework-agnostic
 * 
 * Pattern: Decorator pattern with transactional wrappers
 * - Pure services are instantiated (framework-agnostic)
 * - Wrapped with transactional behavior (infrastructure concern)
 * 
 * ACL Pattern: ProductAccessPort isolates Cart from Product context
 * - Cart services depend on ProductAccessPort (application layer)
 * - ProductAccessAdapter translates Product domain to Cart DTOs (infrastructure layer)
 */
@Configuration
public class CartConfiguration {
    
    // ==================== Mappers ====================
    
    @Bean
    public CartPersistenceMapper cartPersistenceMapper() {
        return new CartPersistenceMapper();
    }
    
    // ==================== Output Ports (Adapters) ====================
    
    @Bean
    public CartRepositoryPort cartRepositoryPort(JpaCartRepository jpaCartRepository,
                                                CartPersistenceMapper mapper) {
        return new CartRepositoryAdapter(jpaCartRepository, mapper);
    }
    
    @Bean
    public CartEventPublisherPort cartEventPublisherPort(ApplicationEventPublisher eventPublisher) {
        return new CartEventPublisherAdapter(eventPublisher);
    }
    
    /**
     * Anti-Corruption Layer: ProductAccessPort
     * Isolates Cart bounded context from Product bounded context
     * Translates Product domain objects to Cart's own DTOs
     */
    @Bean
    public ProductAccessPort productAccessPort(GetProductByIdUseCase getProductByIdUseCase) {
        return new ProductAccessAdapter(getProductByIdUseCase);
    }
    
    // ==================== Application Services (Use Cases) ====================
    // Note: Services are wrapped with transactional behavior at infrastructure boundary
    
    /**
     * Add Item to Cart Use Case
     * Handles adding products to a shopping cart
     */
    @Bean
    public AddItemToCartUseCase addItemToCartUseCase(CartRepositoryPort cartRepository,
                                                     ProductAccessPort productAccessPort,
                                                     CartEventPublisherPort eventPublisher) {
        AddItemToCartUseCase service = new AddItemToCartService(cartRepository, productAccessPort, eventPublisher);
        return new TransactionalUseCaseWrapper.TransactionalAddItemToCartUseCase(service);
    }
    
    /**
     * Remove Item from Cart Use Case
     * Handles removing products from a shopping cart
     */
    @Bean
    public RemoveItemFromCartUseCase removeItemFromCartUseCase(CartRepositoryPort cartRepository,
                                                               CartEventPublisherPort eventPublisher) {
        RemoveItemFromCartUseCase service = new RemoveItemFromCartService(cartRepository, eventPublisher);
        return new TransactionalUseCaseWrapper.TransactionalRemoveItemFromCartUseCase(service);
    }
    
    /**
     * Update Cart Item Quantity Use Case
     * Handles updating the quantity of items in a shopping cart
     */
    @Bean
    public UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase(CartRepositoryPort cartRepository,
                                                                       ProductAccessPort productAccessPort) {
        UpdateCartItemQuantityUseCase service = new UpdateCartItemQuantityService(cartRepository, productAccessPort);
        return new TransactionalUseCaseWrapper.TransactionalUpdateCartItemQuantityUseCase(service);
    }
    
    /**
     * Get Cart Use Case
     * Handles retrieving carts by ID or user ID
     */
    @Bean
    public GetCartUseCase getCartUseCase(CartRepositoryPort cartRepository) {
        GetCartUseCase service = new GetCartService(cartRepository);
        return new TransactionalUseCaseWrapper.TransactionalGetCartUseCase(service);
    }
    
    /**
     * Clear Cart Use Case
     * Handles clearing all items from a shopping cart
     */
    @Bean
    public ClearCartUseCase clearCartUseCase(CartRepositoryPort cartRepository,
                                             CartEventPublisherPort eventPublisher) {
        ClearCartUseCase service = new ClearCartService(cartRepository, eventPublisher);
        return new TransactionalUseCaseWrapper.TransactionalClearCartUseCase(service);
    }
}
