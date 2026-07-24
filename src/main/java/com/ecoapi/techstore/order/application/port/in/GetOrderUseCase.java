package com.ecoapi.techstore.order.application.port.in;

import com.ecoapi.techstore.order.application.service.dto.GetOrderQuery;
import com.ecoapi.techstore.order.domain.model.Order;

import java.util.List;

/**
 * Input Port (Use Case) for retrieving orders
 * Implements the Query pattern for order retrieval
 */
public interface GetOrderUseCase {
    
    /**
     * Get an order by its ID
     * @param query The query containing the order ID
     * @return The order
     */
    Order getOrder(GetOrderQuery query);
    
    /**
     * Get all orders for a user
     * @param query The query containing the user ID
     * @return List of orders for the user
     */
    List<Order> getOrders(GetOrderQuery query);
    
    /**
     * @deprecated Use getOrder(GetOrderQuery.forOrder(orderId)) instead
     */
    @Deprecated(forRemoval = true)
    default Order getOrderById(Long orderId) {
        return getOrder(GetOrderQuery.forOrder(orderId));
    }
    
    /**
     * @deprecated Use getOrders(GetOrderQuery.forUser(userId)) instead
     */
    @Deprecated(forRemoval = true)
    default List<Order> getOrdersByUserId(Long userId) {
        return getOrders(GetOrderQuery.forUser(userId));
    }
}
