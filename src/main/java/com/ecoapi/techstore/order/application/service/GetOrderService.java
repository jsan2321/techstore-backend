package com.ecoapi.techstore.order.application.service;

import com.ecoapi.techstore.order.application.port.in.GetOrderUseCase;
import com.ecoapi.techstore.order.application.port.out.OrderRepositoryPort;
import com.ecoapi.techstore.order.application.service.dto.GetOrderQuery;
import com.ecoapi.techstore.order.domain.exception.OrderNotFoundException;
import com.ecoapi.techstore.order.domain.model.Order;
import com.ecoapi.techstore.order.domain.model.OrderId;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;

import java.util.List;

/**
 * Application Service for retrieving orders
 * Single Responsibility: Handle order retrieval business logic
 */
public class GetOrderService implements GetOrderUseCase {
    
    private final OrderRepositoryPort orderRepository;
    
    public GetOrderService(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    @Override
    public Order getOrder(GetOrderQuery query) {
        if (query.orderId() != null) {
            return orderRepository.findById(OrderId.of(query.orderId()))
                    .orElseThrow(() -> new OrderNotFoundException(
                            "Order not found with id: " + query.orderId()));
        }
        
        // If userId is provided but orderId is not, throw exception
        // as this method should return a single order
        throw new IllegalArgumentException("Order ID must be provided to get a single order");
    }
    
    @Override
    public List<Order> getOrders(GetOrderQuery query) {
        // Fetch all orders (admin use)
        if (query.fetchAll()) {
            return orderRepository.findAll();
        }
        
        if (query.userId() != null) {
            return orderRepository.findByUserId(UserId.of(query.userId()));
        }
        
        // If orderId is provided, return a single-element list
        if (query.orderId() != null) {
            Order order = getOrder(query);
            return List.of(order);
        }
        
        throw new IllegalArgumentException("Either orderId, userId must be provided, or fetchAll must be true");
    }
}
