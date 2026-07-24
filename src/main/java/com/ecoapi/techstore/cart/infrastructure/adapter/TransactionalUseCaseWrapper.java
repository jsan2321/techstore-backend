package com.ecoapi.techstore.cart.infrastructure.adapter;

import com.ecoapi.techstore.cart.application.port.in.*;
import com.ecoapi.techstore.cart.application.service.dto.*;
import com.ecoapi.techstore.cart.domain.model.Cart;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional wrapper for cart use cases
 * Adds Spring's transaction management at the infrastructure boundary
 * Keeps application layer framework-agnostic
 */
public class TransactionalUseCaseWrapper {
    
    /**
     * Wraps AddItemToCartUseCase with transactional behavior
     */
    public static class TransactionalAddItemToCartUseCase implements AddItemToCartUseCase {
        private final AddItemToCartUseCase delegate;
        
        public TransactionalAddItemToCartUseCase(AddItemToCartUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional
        public Cart addItem(AddItemCommand command) {
            return delegate.addItem(command);
        }
    }
    
    /**
     * Wraps RemoveItemFromCartUseCase with transactional behavior
     */
    public static class TransactionalRemoveItemFromCartUseCase implements RemoveItemFromCartUseCase {
        private final RemoveItemFromCartUseCase delegate;
        
        public TransactionalRemoveItemFromCartUseCase(RemoveItemFromCartUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional
        public Cart removeItem(RemoveItemCommand command) {
            return delegate.removeItem(command);
        }
    }
    
    /**
     * Wraps UpdateCartItemQuantityUseCase with transactional behavior
     */
    public static class TransactionalUpdateCartItemQuantityUseCase implements UpdateCartItemQuantityUseCase {
        private final UpdateCartItemQuantityUseCase delegate;
        
        public TransactionalUpdateCartItemQuantityUseCase(UpdateCartItemQuantityUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional
        public Cart updateItemQuantity(UpdateItemQuantityCommand command) {
            return delegate.updateItemQuantity(command);
        }
    }
    
    /**
     * Wraps GetCartUseCase with transactional behavior
     */
    public static class TransactionalGetCartUseCase implements GetCartUseCase {
        private final GetCartUseCase delegate;
        
        public TransactionalGetCartUseCase(GetCartUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional
        public Cart getCart(GetCartQuery query) {
            // Not always read-only because it may create a new cart for userId queries
            return delegate.getCart(query);
        }
    }
    
    /**
     * Wraps ClearCartUseCase with transactional behavior
     */
    public static class TransactionalClearCartUseCase implements ClearCartUseCase {
        private final ClearCartUseCase delegate;
        
        public TransactionalClearCartUseCase(ClearCartUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional
        public void clearCart(ClearCartCommand command) {
            delegate.clearCart(command);
        }
    }
}
