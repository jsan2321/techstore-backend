package com.ecoapi.techstore.order.application.service;

import com.ecoapi.techstore.order.application.port.in.UpdateOrderStatusUseCase;
import com.ecoapi.techstore.order.application.port.out.OrderEventPublisherPort;
import com.ecoapi.techstore.order.application.port.out.OrderRepositoryPort;
import com.ecoapi.techstore.order.application.port.out.StockReservationPort;
import com.ecoapi.techstore.order.application.service.dto.UpdateOrderStatusCommand;
import com.ecoapi.techstore.order.domain.events.OrderCancelledEvent;
import com.ecoapi.techstore.order.domain.events.OrderStatusChangedEvent;
import com.ecoapi.techstore.order.domain.exception.OrderNotFoundException;
import com.ecoapi.techstore.order.domain.model.Order;
import com.ecoapi.techstore.order.domain.model.OrderId;
import com.ecoapi.techstore.order.domain.model.OrderItem;
import com.ecoapi.techstore.order.domain.model.OrderStatus;

/**
 * Application Service for updating order status
 * Single Responsibility: Handle order status change business logic
 */
public class UpdateOrderStatusService implements UpdateOrderStatusUseCase {
    
    private final OrderRepositoryPort orderRepository;
    private final StockReservationPort stockReservationPort;
    private final OrderEventPublisherPort eventPublisher;
    
    public UpdateOrderStatusService(OrderRepositoryPort orderRepository,
                                   StockReservationPort stockReservationPort,
                                   OrderEventPublisherPort eventPublisher) {
        this.orderRepository = orderRepository;
        this.stockReservationPort = stockReservationPort;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public Order execute(UpdateOrderStatusCommand command) {
        Order order = orderRepository.findById(OrderId.of(command.orderId()))
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found with id: " + command.orderId()));
        
        OrderStatus oldStatus = order.getStatus();
        
        // Apply status change based on target status
        switch (command.targetStatus()) {
            case PROCESSING -> order.process();
            case SHIPPED -> order.ship();
            case DELIVERED -> order.deliver();
            case CANCELLED -> {
                handleCancellation(order, command.reason());
            }
            default -> throw new IllegalArgumentException(
                    "Unsupported status transition to: " + command.targetStatus());
        }
        
        Order savedOrder = orderRepository.save(order);
        
        // Publish appropriate event
        publishStatusChangeEvent(savedOrder, oldStatus, command.reason());
        
        return savedOrder;
    }
    
    private void handleCancellation(Order order, String reason) {
        order.cancel();
        
        // Release reserved stock for cancelled orders
        for (OrderItem item : order.getOrderItems()) {
            stockReservationPort.releaseStock(item.getProductId().value(), item.getQuantity());
        }
    }
    
    private void publishStatusChangeEvent(Order order, OrderStatus oldStatus, String reason) {
        if (order.getStatus() == OrderStatus.CANCELLED) {
            OrderCancelledEvent event = new OrderCancelledEvent(
                    order.getId(),
                    order.getUserId().value(),
                    reason != null ? reason : "Order cancelled"
            );
            eventPublisher.publish(event);
        } else {
            OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                    order.getId(),
                    oldStatus,
                    order.getStatus()
            );
            eventPublisher.publish(event);
        }
    }
}
