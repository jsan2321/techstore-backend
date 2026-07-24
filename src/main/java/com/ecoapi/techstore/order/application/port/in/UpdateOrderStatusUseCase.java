package com.ecoapi.techstore.order.application.port.in;

import com.ecoapi.techstore.order.application.service.dto.UpdateOrderStatusCommand;
import com.ecoapi.techstore.order.domain.model.Order;

/**
 * Input Port (Use Case) for updating order status
 * Implements the Command pattern for order status changes
 */
public interface UpdateOrderStatusUseCase {
    
    /**
     * Update order status based on the command
     * @param command The command containing status change details
     * @return The updated order
     */
    Order execute(UpdateOrderStatusCommand command);
    
    /**
     * @deprecated Use execute(UpdateOrderStatusCommand.process(orderId)) instead
     */
    @Deprecated(forRemoval = true)
    default Order processOrder(Long orderId) {
        return execute(UpdateOrderStatusCommand.process(orderId));
    }
    
    /**
     * @deprecated Use execute(UpdateOrderStatusCommand.ship(orderId)) instead
     */
    @Deprecated(forRemoval = true)
    default Order shipOrder(Long orderId) {
        return execute(UpdateOrderStatusCommand.ship(orderId));
    }
    
    /**
     * @deprecated Use execute(UpdateOrderStatusCommand.deliver(orderId)) instead
     */
    @Deprecated(forRemoval = true)
    default Order deliverOrder(Long orderId) {
        return execute(UpdateOrderStatusCommand.deliver(orderId));
    }
    
    /**
     * @deprecated Use execute(UpdateOrderStatusCommand.cancel(orderId, reason)) instead
     */
    @Deprecated(forRemoval = true)
    default Order cancelOrder(Long orderId) {
        return execute(UpdateOrderStatusCommand.cancel(orderId, null));
    }
}
