package com.ecoapi.techstore.cart.domain.model;

import com.ecoapi.techstore.cart.domain.exception.CartItemNotFoundException;
import com.ecoapi.techstore.common.domain.valueobjects.Money;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Cart Aggregate Root - Pure domain model
 * Contains business logic and invariants for shopping cart
 */
public class Cart {
    
    private CartId id;
    private UserId userId;
    private List<CartItem> items;
    private Money totalAmount;
    
    // Private constructor - use factory methods
    private Cart(UserId userId) {
        validateUserId(userId);
        this.userId = userId;
        this.items = new ArrayList<>();
        this.totalAmount = Money.zero();
    }
    
    // Private constructor for reconstitution - use factory methods
    private Cart(CartId id, UserId userId, List<CartItem> items, Money totalAmount) {
        this.id = id;
        this.userId = userId;
        this.items = new ArrayList<>(items != null ? items : new ArrayList<>());
        this.totalAmount = totalAmount != null ? totalAmount : Money.zero();
    }
    
    // Factory Methods
    
    /**
     * Factory method for creating a new cart for a user
     * Creates an empty cart ready for items to be added
     */
    public static Cart createFor(UserId userId) {
        return new Cart(userId);
    }
    
    /**
     * Factory method for reconstituting cart from persistence
     * Used by infrastructure layer to rebuild domain object from database
     */
    public static Cart reconstitute(CartId id, UserId userId, List<CartItem> items, Money totalAmount) {
        return new Cart(id, userId, items, totalAmount);
    }
    
    // Business logic methods
    public void addItem(CartItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Cart item cannot be null");
        }
        
        Optional<CartItem> existingItem = findItemByProductId(item.getProductId());
        if (existingItem.isPresent()) {
            CartItem existingCartItem = existingItem.get();
            existingCartItem.updateUnitPrice(item.getUnitPrice());
            existingCartItem.increaseQuantity(item.getQuantity());
        } else {
            items.add(item);
        }
        
        recalculateTotalAmount();
    }
    
    public void removeItem(ProductId productId) {
        CartItem item = findItemByProductId(productId)
                .orElseThrow(() -> new CartItemNotFoundException("Item not found in cart"));
        
        items.remove(item);
        recalculateTotalAmount();
    }
    
    public void updateItemQuantity(ProductId productId, int newQuantity) {
        CartItem item = findItemByProductId(productId)
                .orElseThrow(() -> new CartItemNotFoundException("Item not found in cart"));
        
        item.updateQuantity(newQuantity);
        recalculateTotalAmount();
    }
    
    public void updateItemPrice(ProductId productId, Money newPrice) {
        CartItem item = findItemByProductId(productId)
                .orElseThrow(() -> new CartItemNotFoundException("Item not found in cart"));
        
        item.updateUnitPrice(newPrice);
        recalculateTotalAmount();
    }
    
    public void clear() {
        items.clear();
        this.totalAmount = Money.zero();
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    public boolean hasItem(ProductId productId) {
        return findItemByProductId(productId).isPresent();
    }
    
    public int getItemCount() {
        return items.size();
    }
    
    public int getTotalItems() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    
    private Optional<CartItem> findItemByProductId(ProductId productId) {
        return items.stream()
                .filter(item -> item.isForProduct(productId))
                .findFirst();
    }
    
    private void recalculateTotalAmount() {
        this.totalAmount = items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(Money.zero(), Money::add);
    }
    
    private void validateUserId(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
    
    // Getters
    public CartId getId() {
        return id;
    }
    
    public void setId(CartId id) {
        this.id = id;
    }
    
    public UserId getUserId() {
        return userId;
    }
    
    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }
    
    public Money getTotalAmount() {
        return totalAmount;
    }
}
