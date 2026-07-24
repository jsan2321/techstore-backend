package com.ecoapi.techstore.order.application.port.in;

import com.ecoapi.techstore.order.application.service.dto.PlaceOrderCommand;
import com.ecoapi.techstore.order.domain.model.Order;

/**
 * Input Port (Use Case) for placing a new order.
 * Implements the Command pattern for order creation.
 */
public interface PlaceOrderUseCase {
    
    /**
     * Place a new order based on the user's cart.
     * 
     * @param command The command containing order placement details including
     *                user ID, shipping address, and payment method
     * @return The created order
     */
    Order execute(PlaceOrderCommand command);
}
