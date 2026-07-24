package com.ecoapi.techstore.order.application.service;

import com.ecoapi.techstore.order.application.port.in.InitiatePayPalPaymentUseCase;
import com.ecoapi.techstore.order.application.port.out.OrderRepositoryPort;
import com.ecoapi.techstore.order.application.port.out.PaymentProviderPort;
import com.ecoapi.techstore.order.application.service.dto.InitiatePayPalPaymentCommand;
import com.ecoapi.techstore.order.application.service.dto.InitiatePayPalPaymentResult;
import com.ecoapi.techstore.order.domain.exception.OrderNotFoundException;
import com.ecoapi.techstore.order.domain.model.Order;
import com.ecoapi.techstore.order.domain.model.OrderId;
import com.ecoapi.techstore.order.domain.valueobjects.ShippingAddress;

public class InitiatePayPalPaymentService implements InitiatePayPalPaymentUseCase {

    private static final String PAYPAL_CURRENCY = "USD";

    private final OrderRepositoryPort orderRepository;
    private final PaymentProviderPort paymentProvider;

    public InitiatePayPalPaymentService(OrderRepositoryPort orderRepository,
                                        PaymentProviderPort paymentProvider) {
        this.orderRepository = orderRepository;
        this.paymentProvider = paymentProvider;
    }

    @Override
    public InitiatePayPalPaymentResult execute(InitiatePayPalPaymentCommand command) {
        Order order = orderRepository.findById(OrderId.of(command.orderId()))
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + command.orderId()));

        if (!order.isPayPalPayment()) {
            throw new IllegalStateException("Order does not use PAYPAL as payment method");
        }
        if (order.getId() == null) {
            throw new IllegalStateException("Order must be persisted before initiating payment");
        }

        ShippingAddress orderShippingAddress = order.getShippingAddress();
        PaymentProviderPort.PayPalShippingAddress payPalShippingAddress = null;
        if (orderShippingAddress != null) {
            payPalShippingAddress = new PaymentProviderPort.PayPalShippingAddress(
                orderShippingAddress.fullName(),
                orderShippingAddress.street(),
                orderShippingAddress.addressLine2(),
                orderShippingAddress.city(),
                orderShippingAddress.state(),
                orderShippingAddress.postalCode(),
                orderShippingAddress.country()
            );
        }

        PaymentProviderPort.CreatePayPalOrderResult providerOrder = paymentProvider.createPayPalOrder(
                order.getId().getValue(),
                order.getTotalAmount().value(),
            PAYPAL_CURRENCY,
            payPalShippingAddress
        );

        order.initiatePayPalPayment(providerOrder.providerOrderId());
        Order savedOrder = orderRepository.save(order);

        return new InitiatePayPalPaymentResult(
                savedOrder.getId().getValue(),
                providerOrder.providerOrderId(),
                providerOrder.approvalUrl()
        );
    }
}
