package com.ecoapi.techstore.order.infrastructure.adapter;

import com.ecoapi.techstore.order.application.port.in.GetOrderUseCase;
import com.ecoapi.techstore.order.application.port.in.CapturePayPalPaymentUseCase;
import com.ecoapi.techstore.order.application.port.in.InitiatePayPalPaymentUseCase;
import com.ecoapi.techstore.order.application.port.in.PlaceOrderUseCase;
import com.ecoapi.techstore.order.application.port.in.UpdateOrderStatusUseCase;
import com.ecoapi.techstore.order.application.service.dto.CapturePayPalPaymentCommand;
import com.ecoapi.techstore.order.application.service.dto.GetOrderQuery;
import com.ecoapi.techstore.order.application.service.dto.InitiatePayPalPaymentCommand;
import com.ecoapi.techstore.order.application.service.dto.PlaceOrderCommand;
import com.ecoapi.techstore.order.application.service.dto.UpdateOrderStatusCommand;
import com.ecoapi.techstore.order.domain.model.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Transactional wrappers for Order use cases
 * Adds Spring's transaction management at the infrastructure boundary
 * Keeps application layer framework-agnostic
 * 
 * Transaction boundaries:
 * - PlaceOrder: Single transaction for stock reservation, order creation, and cart clearing
 * - UpdateOrderStatus: Single transaction for status update and stock release (if cancelled)
 * - GetOrder: Read-only transaction for consistency
 */
public class TransactionalOrderUseCaseWrapper {
    
    /**
     * Wraps PlaceOrderUseCase with transactional behavior
     * Ensures atomicity of: stock reservation -> order save -> cart clear
     */
    public static class TransactionalPlaceOrderUseCase implements PlaceOrderUseCase {
        private final PlaceOrderUseCase delegate;
        
        public TransactionalPlaceOrderUseCase(PlaceOrderUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional
        public Order execute(PlaceOrderCommand command) {
            return delegate.execute(command);
        }
    }
    
    /**
     * Wraps UpdateOrderStatusUseCase with transactional behavior
     * Ensures atomicity of: status update -> stock release (on cancellation)
     */
    public static class TransactionalUpdateOrderStatusUseCase implements UpdateOrderStatusUseCase {
        private final UpdateOrderStatusUseCase delegate;
        
        public TransactionalUpdateOrderStatusUseCase(UpdateOrderStatusUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional
        public Order execute(UpdateOrderStatusCommand command) {
            return delegate.execute(command);
        }
    }
    
    /**
     * Wraps GetOrderUseCase with read-only transactional behavior
     * Provides consistent reads and allows database optimizations
     */
    public static class TransactionalGetOrderUseCase implements GetOrderUseCase {
        private final GetOrderUseCase delegate;
        
        public TransactionalGetOrderUseCase(GetOrderUseCase delegate) {
            this.delegate = delegate;
        }
        
        @Override
        @Transactional(readOnly = true)
        public Order getOrder(GetOrderQuery query) {
            return delegate.getOrder(query);
        }
        
        @Override
        @Transactional(readOnly = true)
        public List<Order> getOrders(GetOrderQuery query) {
            return delegate.getOrders(query);
        }
    }

    /**
     * Wraps InitiatePayPalPaymentUseCase with transactional behavior
     */
    public static class TransactionalInitiatePayPalPaymentUseCase implements InitiatePayPalPaymentUseCase {
        private final InitiatePayPalPaymentUseCase delegate;

        public TransactionalInitiatePayPalPaymentUseCase(InitiatePayPalPaymentUseCase delegate) {
            this.delegate = delegate;
        }

        @Override
        @Transactional
        public com.ecoapi.techstore.order.application.service.dto.InitiatePayPalPaymentResult execute(
                InitiatePayPalPaymentCommand command) {
            return delegate.execute(command);
        }
    }

    /**
     * Wraps CapturePayPalPaymentUseCase with transactional behavior
     */
    public static class TransactionalCapturePayPalPaymentUseCase implements CapturePayPalPaymentUseCase {
        private final CapturePayPalPaymentUseCase delegate;

        public TransactionalCapturePayPalPaymentUseCase(CapturePayPalPaymentUseCase delegate) {
            this.delegate = delegate;
        }

        @Override
        @Transactional
        public Order execute(CapturePayPalPaymentCommand command) {
            return delegate.execute(command);
        }
    }
}
