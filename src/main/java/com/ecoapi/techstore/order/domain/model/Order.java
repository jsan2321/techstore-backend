package com.ecoapi.techstore.order.domain.model;

import com.ecoapi.techstore.order.domain.exception.InvalidOrderStatusTransitionException;
import com.ecoapi.techstore.order.domain.valueobjects.PaymentTransaction;
import com.ecoapi.techstore.order.domain.valueobjects.ShippingAddress;
import com.ecoapi.techstore.common.domain.valueobjects.Money;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Order Aggregate Root - Pure domain model
 * Contains business logic and invariants for orders
 * 
 * An Order represents a legal contract at a specific point in time,
 * so it captures the shipping address and payment method at order placement.
 */
public class Order {
    
    private OrderId id;
    private UserId userId;
    private List<OrderItem> orderItems;
    private Money totalAmount;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private ShippingAddress shippingAddress;
    private String paymentMethod;
    private PaymentTransaction paymentTransaction;
    
    // Private constructor - use factory methods
    private Order(UserId userId, List<OrderItem> orderItems, 
                  ShippingAddress shippingAddress, String paymentMethod) {
        validateUserId(userId);
        validateOrderItems(orderItems);
        validateShippingAddress(shippingAddress);
        validatePaymentMethod(paymentMethod);
        
        this.userId = userId;
        this.orderItems = new ArrayList<>(orderItems);
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.status = OrderStatus.PENDING;
        this.orderDate = LocalDateTime.now();
        this.totalAmount = calculateTotalAmount();
        this.paymentTransaction = null;
    }
    
    // Private constructor for reconstitution - use factory methods
    private Order(OrderId id, UserId userId, List<OrderItem> orderItems, 
                  Money totalAmount, OrderStatus status, LocalDateTime orderDate,
                  ShippingAddress shippingAddress, String paymentMethod,
                  PaymentTransaction paymentTransaction) {
        this.id = id;
        this.userId = userId;
        this.orderItems = new ArrayList<>(orderItems != null ? orderItems : new ArrayList<>());
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderDate = orderDate;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.paymentTransaction = paymentTransaction;
    }
    
    // Factory Methods
    
    /**
     * Factory method for placing a new order
     * Creates an order in PENDING status with captured shipping address
     */
    public static Order place(UserId userId, List<OrderItem> orderItems,
                              ShippingAddress shippingAddress, String paymentMethod) {
        return new Order(userId, orderItems, shippingAddress, paymentMethod);
    }
    
    /**
     * Factory method for reconstituting order from persistence
     * Used by infrastructure layer to rebuild domain object from database
     */
    public static Order reconstitute(OrderId id, UserId userId, List<OrderItem> orderItems,
                                     Money totalAmount, OrderStatus status, LocalDateTime orderDate,
                                     ShippingAddress shippingAddress, String paymentMethod,
                                     PaymentTransaction paymentTransaction) {
        return new Order(id, userId, orderItems, totalAmount, status, orderDate, 
                        shippingAddress, paymentMethod, paymentTransaction);
    }
    
    // Business logic methods
    public void process() {
        if (isPayPalPayment() && !isPaymentCaptured()) {
            throw new IllegalStateException("PayPal payment must be captured before processing the order");
        }
        changeStatus(OrderStatus.PROCESSING);
    }
    
    public void ship() {
        changeStatus(OrderStatus.SHIPPED);
    }
    
    public void deliver() {
        changeStatus(OrderStatus.DELIVERED);
    }
    
    public void cancel() {
        if (!status.canBeCancelled()) {
            throw new InvalidOrderStatusTransitionException(
                "Order cannot be cancelled in " + status + " status");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public void initiatePayPalPayment(String providerOrderId) {
        if (!isPayPalPayment()) {
            throw new IllegalStateException("Order payment method is not PAYPAL");
        }
        if (!isPending()) {
            throw new IllegalStateException("PayPal payment can only be initiated while order is pending");
        }
        if (paymentTransaction != null && !paymentTransaction.isFailed()) {
            throw new IllegalStateException("PayPal payment is already initiated or captured for this order");
        }
        paymentTransaction = PaymentTransaction.initiated("PAYPAL", providerOrderId, LocalDateTime.now());
    }

    public void capturePayPalPayment(String providerOrderId, String captureId) {
        if (!isPayPalPayment()) {
            throw new IllegalStateException("Order payment method is not PAYPAL");
        }
        if (paymentTransaction == null) {
            throw new IllegalStateException("PayPal payment has not been initiated for this order");
        }
        if (!paymentTransaction.providerOrderId().equals(providerOrderId)) {
            throw new IllegalStateException("PayPal order id does not match initiated payment");
        }
        paymentTransaction = paymentTransaction.captured(captureId, LocalDateTime.now());
    }

    public void failPayPalPayment(String providerOrderId, String reason) {
        if (!isPayPalPayment()) {
            throw new IllegalStateException("Order payment method is not PAYPAL");
        }
        if (paymentTransaction == null) {
            throw new IllegalStateException("PayPal payment has not been initiated for this order");
        }
        if (!paymentTransaction.providerOrderId().equals(providerOrderId)) {
            throw new IllegalStateException("PayPal order id does not match initiated payment");
        }
        paymentTransaction = paymentTransaction.failed(reason, LocalDateTime.now());
    }

    public boolean isPayPalPayment() {
        return "PAYPAL".equalsIgnoreCase(paymentMethod);
    }

    public boolean isPaymentCaptured() {
        return !isPayPalPayment() || (paymentTransaction != null && paymentTransaction.isCaptured());
    }
    
    private void changeStatus(OrderStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new InvalidOrderStatusTransitionException(
                "Cannot transition from " + status + " to " + newStatus);
        }
        this.status = newStatus;
    }
    
    public boolean canBeCancelled() {
        return status.canBeCancelled();
    }
    
    public boolean isFinal() {
        return status.isFinal();
    }
    
    public boolean isPending() {
        return status == OrderStatus.PENDING;
    }
    
    public boolean isDelivered() {
        return status == OrderStatus.DELIVERED;
    }
    
    public boolean isCancelled() {
        return status == OrderStatus.CANCELLED;
    }
    
    public int getTotalItems() {
        return orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }
    
    private Money calculateTotalAmount() {
        return orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(Money.zero(), Money::add);
    }
    
    private void validateUserId(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }
    
    private void validateOrderItems(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
    }
    
    private void validateShippingAddress(ShippingAddress shippingAddress) {
        if (shippingAddress == null) {
            throw new IllegalArgumentException("Shipping address cannot be null");
        }
    }
    
    private void validatePaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException("Payment method cannot be null or empty");
        }
    }
    
    // Getters
    public OrderId getId() {
        return id;
    }
    
    public void setId(OrderId id) {
        this.id = id;
    }
    
    public UserId getUserId() {
        return userId;
    }
    
    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
    }
    
    public Money getTotalAmount() {
        return totalAmount;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentTransaction getPaymentTransaction() {
        return paymentTransaction;
    }
}
