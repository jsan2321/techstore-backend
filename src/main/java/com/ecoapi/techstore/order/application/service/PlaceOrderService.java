package com.ecoapi.techstore.order.application.service;

import com.ecoapi.techstore.order.application.port.in.PlaceOrderUseCase;
import com.ecoapi.techstore.order.application.port.out.CartAccessPort;
import com.ecoapi.techstore.order.application.port.out.OrderEventPublisherPort;
import com.ecoapi.techstore.order.application.port.out.OrderRepositoryPort;
import com.ecoapi.techstore.order.application.port.out.StockReservationPort;
import com.ecoapi.techstore.order.application.port.out.UserAddressPort;
import com.ecoapi.techstore.order.application.port.out.UserValidationPort;
import com.ecoapi.techstore.order.application.port.out.dto.CartData;
import com.ecoapi.techstore.order.application.port.out.dto.CartItemData;
import com.ecoapi.techstore.order.application.port.out.dto.UserAddressData;
import com.ecoapi.techstore.order.application.service.dto.PlaceOrderCommand;
import com.ecoapi.techstore.order.application.service.dto.ShippingAddressData;
import com.ecoapi.techstore.order.domain.events.OrderPlacedEvent;
import com.ecoapi.techstore.order.domain.exception.InsufficientStockException;
import com.ecoapi.techstore.order.domain.model.Order;
import com.ecoapi.techstore.order.domain.model.OrderItem;
import com.ecoapi.techstore.order.domain.valueobjects.ShippingAddress;
import com.ecoapi.techstore.common.domain.valueobjects.Money;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Application Service for placing orders
 * Single Responsibility: Handle order creation business logic
 * 
 * This service uses output ports to communicate with other bounded contexts,
 * following Hexagonal Architecture principles for proper context isolation.
 * 
 * If no shipping address is provided in the command, the service will attempt
 * to use the user's saved profile address.
 */
public class PlaceOrderService implements PlaceOrderUseCase {
    
    private final OrderRepositoryPort orderRepository;
    private final CartAccessPort cartAccessPort;
    private final StockReservationPort stockReservationPort;
    private final UserValidationPort userValidationPort;
    private final UserAddressPort userAddressPort;
    private final OrderEventPublisherPort eventPublisher;
    
    public PlaceOrderService(OrderRepositoryPort orderRepository,
                            CartAccessPort cartAccessPort,
                            StockReservationPort stockReservationPort,
                            UserValidationPort userValidationPort,
                            UserAddressPort userAddressPort,
                            OrderEventPublisherPort eventPublisher) {
        this.orderRepository = orderRepository;
        this.cartAccessPort = cartAccessPort;
        this.stockReservationPort = stockReservationPort;
        this.userValidationPort = userValidationPort;
        this.userAddressPort = userAddressPort;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public Order execute(PlaceOrderCommand command) {
        // Validate user exists and is active
        if (!userValidationPort.isValidUser(command.userId())) {
            throw new IllegalStateException("User is not valid or does not exist");
        }
        
        // Resolve shipping address (from command or user profile)
        ShippingAddress shippingAddress = resolveShippingAddress(command);
        
        // Get user's cart via output port (not directly accessing Cart context)
        CartData cartData = cartAccessPort.getCartForUser(command.userId());
        
        if (cartData.isEmpty()) {
            throw new IllegalStateException("Cannot place order with empty cart");
        }
        
        // Validate stock and create order items
        List<OrderItem> orderItems = new ArrayList<>();
        List<CartItemData> reservedItems = new ArrayList<>();
        
        try {
            for (CartItemData cartItem : cartData.items()) {
                // Validate and reserve stock
                validateAndReserveStock(cartItem);
                reservedItems.add(cartItem);
                
                // Create order item
                orderItems.add(createOrderItem(cartItem));
            }
        } catch (InsufficientStockException e) {
            // Rollback reserved stock on failure
            for (CartItemData reservedItem : reservedItems) {
                stockReservationPort.releaseStock(reservedItem.productId(), reservedItem.quantity());
            }
            throw e;
        }
        
        // Create order using factory method with shipping address
        Order order = Order.place(
                UserId.of(command.userId()), 
                orderItems,
                shippingAddress,
                command.paymentMethod()
        );
        Order savedOrder = orderRepository.save(order);
        
        // Clear cart after successful order (via output port)
        cartAccessPort.clearCart(cartData.cartId());
        
        // Publish domain event
        publishOrderPlacedEvent(savedOrder);
        
        return savedOrder;
    }
    
    /**
     * Resolves the shipping address for the order.
     * Priority:
     * 1. Explicit address from command (if provided)
     * 2. User's saved profile address (if available)
     * 3. Throws exception if neither is available
     */
    private ShippingAddress resolveShippingAddress(PlaceOrderCommand command) {
        // If command has explicit address, use it
        if (command.hasExplicitAddress()) {
            ShippingAddressData addr = command.shippingAddress();
            return ShippingAddress.of(
                    addr.fullName(),
                    addr.street(),
                    addr.addressLine2(),
                    addr.city(),
                    addr.state(),
                    addr.postalCode(),
                    addr.country(),
                    addr.deliveryNotes()
            );
        }

        // Try to get address from user profile
        Optional<UserAddressData> profileAddress = userAddressPort.getUserAddress(command.userId());

        if (profileAddress.isPresent() && profileAddress.get().isComplete()) {
            UserAddressData addr = profileAddress.get();
            return ShippingAddress.of(
                    addr.recipientName(),
                    addr.street(),
                    addr.addressLine2(),
                    addr.city(),
                    addr.state(),
                    addr.zipCode(),
                    addr.country(),
                    command.deliveryNotes()
            );
        }

        // No address available - this is an error
        throw new IllegalStateException(
                "No shipping address provided and user has no saved address. " +
                "Please provide a shipping address or update your profile with a default address."
        );
    }
    
    private void validateAndReserveStock(CartItemData cartItem) {
        if (!stockReservationPort.hasAvailableStock(cartItem.productId(), cartItem.quantity())) {
            String productName = stockReservationPort.getProductName(cartItem.productId());
            throw new InsufficientStockException("Insufficient stock for product: " + productName);
        }
        stockReservationPort.reserveStock(cartItem.productId(), cartItem.quantity());
    }
    
    private OrderItem createOrderItem(CartItemData cartItem) {
        return new OrderItem(
                ProductId.of(cartItem.productId()),
                cartItem.productName(),
                cartItem.productDescription(),
            cartItem.productImageUrl(),
                cartItem.quantity(),
                Money.of(cartItem.unitPrice())
        );
    }
    
    private void publishOrderPlacedEvent(Order order) {
        OrderPlacedEvent event = new OrderPlacedEvent(
                order.getId(),
                order.getUserId().value(),
                order.getTotalAmount().value(),
                order.getTotalItems()
        );
        eventPublisher.publish(event);
    }
}
