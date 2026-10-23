package com.ecoapi.techstore.order.application.service;

import com.ecoapi.techstore.common.domain.valueobjects.Money;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.order.application.service.dto.CapturePayPalPaymentCommand;
import com.ecoapi.techstore.order.application.port.out.OrderRepositoryPort;
import com.ecoapi.techstore.order.application.port.out.PaymentProviderPort;
import com.ecoapi.techstore.order.domain.exception.PaymentProcessingException;
import com.ecoapi.techstore.order.domain.model.Order;
import com.ecoapi.techstore.order.domain.model.OrderId;
import com.ecoapi.techstore.order.domain.model.OrderItem;
import com.ecoapi.techstore.order.domain.valueobjects.ShippingAddress;
import com.ecoapi.techstore.order.infrastructure.adapter.TransactionalOrderUseCaseWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CapturePayPalPaymentServiceTest {

    private OrderRepositoryPort orderRepository;
    private PaymentProviderPort paymentProvider;
    private CapturePayPalPaymentService service;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepositoryPort.class);
        paymentProvider = mock(PaymentProviderPort.class);
        service = new CapturePayPalPaymentService(orderRepository, paymentProvider);
    }

    private Order createInitiatedPayPalOrder(Long orderId, String providerOrderId) {
        OrderItem item = new OrderItem(
                ProductId.of(10L), "Keyboard", "Mechanical", null, 1, Money.of(new BigDecimal("99.99"))
        );
        ShippingAddress address = ShippingAddress.of(
                "Jane Doe", "123 Main St", null, "Metropolis", "NY", "10001", "US", null
        );
        Order order = Order.reconstitute(
                OrderId.of(orderId),
                UserId.of(1L),
                List.of(item),
                Money.of(new BigDecimal("99.99")),
                com.ecoapi.techstore.order.domain.model.OrderStatus.PENDING,
                java.time.LocalDateTime.now(),
                address,
                "PAYPAL",
                null
        );
        order.initiatePayPalPayment(providerOrderId);
        return order;
    }

    @Test
    void persistsFailedPaymentStatusWhenCaptureStatusIsNotCompleted() {
        Long orderId = 1L;
        String providerOrderId = "PAYPAL-ORDER-123";
        Order order = createInitiatedPayPalOrder(orderId, providerOrderId);

        when(orderRepository.findById(OrderId.of(orderId))).thenReturn(Optional.of(order));
        when(paymentProvider.capturePayPalOrder(providerOrderId))
                .thenReturn(new PaymentProviderPort.CapturePayPalOrderResult(providerOrderId, null, "DENIED"));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        CapturePayPalPaymentCommand command = CapturePayPalPaymentCommand.of(orderId, providerOrderId);

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(PaymentProcessingException.class)
                .hasMessageContaining("PayPal payment was not completed");

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, atLeastOnce()).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getPaymentTransaction()).isNotNull();
        assertThat(savedOrder.getPaymentTransaction().isFailed()).isTrue();
        assertThat(savedOrder.getPaymentTransaction().failureReason()).contains("DENIED");
    }

    @Test
    void persistsFailedPaymentStatusWhenProviderThrowsException() {
        Long orderId = 2L;
        String providerOrderId = "PAYPAL-ORDER-456";
        Order order = createInitiatedPayPalOrder(orderId, providerOrderId);

        when(orderRepository.findById(OrderId.of(orderId))).thenReturn(Optional.of(order));
        when(paymentProvider.capturePayPalOrder(providerOrderId))
                .thenThrow(new RuntimeException("PayPal API timeout"));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        CapturePayPalPaymentCommand command = CapturePayPalPaymentCommand.of(orderId, providerOrderId);

        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(PaymentProcessingException.class);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, atLeastOnce()).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getPaymentTransaction()).isNotNull();
        assertThat(savedOrder.getPaymentTransaction().isFailed()).isTrue();
        assertThat(savedOrder.getPaymentTransaction().failureReason()).contains("PayPal API timeout");
    }

    @Test
    void completesPaymentSuccessfullyWhenProviderReturnsCompleted() {
        Long orderId = 3L;
        String providerOrderId = "PAYPAL-ORDER-789";
        Order order = createInitiatedPayPalOrder(orderId, providerOrderId);

        when(orderRepository.findById(OrderId.of(orderId))).thenReturn(Optional.of(order));
        when(paymentProvider.capturePayPalOrder(providerOrderId))
                .thenReturn(new PaymentProviderPort.CapturePayPalOrderResult(providerOrderId, "CAPTURE-999", "COMPLETED"));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        CapturePayPalPaymentCommand command = CapturePayPalPaymentCommand.of(orderId, providerOrderId);
        Order result = service.execute(command);

        assertThat(result.getPaymentTransaction().isCaptured()).isTrue();
        assertThat(result.getPaymentTransaction().providerCaptureId()).isEqualTo("CAPTURE-999");
    }

    @Test
    void transactionalWrapperConfiguresNoRollbackForPaymentProcessingException() throws NoSuchMethodException {
        Method method = TransactionalOrderUseCaseWrapper.TransactionalCapturePayPalPaymentUseCase.class
                .getMethod("execute", CapturePayPalPaymentCommand.class);

        Transactional transactional = method.getAnnotation(Transactional.class);
        assertThat(transactional).isNotNull();
        assertThat(Arrays.asList(transactional.noRollbackFor()))
                .contains(PaymentProcessingException.class);
    }
}
