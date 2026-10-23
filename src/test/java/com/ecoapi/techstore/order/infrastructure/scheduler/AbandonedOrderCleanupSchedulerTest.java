package com.ecoapi.techstore.order.infrastructure.scheduler;

import com.ecoapi.techstore.common.domain.valueobjects.Money;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.order.application.port.out.OrderRepositoryPort;
import com.ecoapi.techstore.order.application.port.out.StockReservationPort;
import com.ecoapi.techstore.order.domain.model.Order;
import com.ecoapi.techstore.order.domain.model.OrderId;
import com.ecoapi.techstore.order.domain.model.OrderItem;
import com.ecoapi.techstore.order.domain.model.OrderStatus;
import com.ecoapi.techstore.order.domain.valueobjects.ShippingAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AbandonedOrderCleanupSchedulerTest {

    private OrderRepositoryPort orderRepository;
    private StockReservationPort stockReservationPort;
    private AbandonedOrderCleanupScheduler scheduler;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepositoryPort.class);
        stockReservationPort = mock(StockReservationPort.class);
        scheduler = new AbandonedOrderCleanupScheduler(orderRepository, stockReservationPort, 30L);
    }

    private Order createPendingOrder(Long orderId, Long productId, int quantity) {
        OrderItem item = new OrderItem(
                ProductId.of(productId), "Mouse", "Ergonomic", null, quantity, Money.of(new BigDecimal("49.99"))
        );
        ShippingAddress address = ShippingAddress.of(
                "Alex Doe", "456 Elm St", null, "Austin", "TX", "78701", "US", null
        );
        return Order.reconstitute(
                OrderId.of(orderId),
                UserId.of(2L),
                List.of(item),
                Money.of(new BigDecimal("49.99")),
                OrderStatus.PENDING,
                LocalDateTime.now().minusMinutes(45),
                address,
                "PAYPAL",
                null
        );
    }

    @Test
    void cancelsExpiredPendingOrdersAndReleasesStock() {
        Order expiredOrder = createPendingOrder(10L, 55L, 2);

        when(orderRepository.findPendingOrdersCreatedBefore(any(LocalDateTime.class)))
                .thenReturn(List.of(expiredOrder));

        scheduler.cleanupAbandonedOrders();

        verify(stockReservationPort).releaseStock(55L, 2);
        assertThat(expiredOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository).save(expiredOrder);
    }

    @Test
    void doesNothingWhenNoExpiredOrdersExist() {
        when(orderRepository.findPendingOrdersCreatedBefore(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        scheduler.cleanupAbandonedOrders();

        verify(stockReservationPort, never()).releaseStock(any(), anyInt());
        verify(orderRepository, never()).save(any());
    }
}
