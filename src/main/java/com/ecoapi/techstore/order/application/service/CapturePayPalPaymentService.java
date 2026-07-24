package com.ecoapi.techstore.order.application.service;

import com.ecoapi.techstore.order.application.port.in.CapturePayPalPaymentUseCase;
import com.ecoapi.techstore.order.application.port.out.OrderRepositoryPort;
import com.ecoapi.techstore.order.application.port.out.PaymentProviderPort;
import com.ecoapi.techstore.order.application.service.dto.CapturePayPalPaymentCommand;
import com.ecoapi.techstore.order.domain.exception.OrderNotFoundException;
import com.ecoapi.techstore.order.domain.exception.PaymentProcessingException;
import com.ecoapi.techstore.order.domain.model.Order;
import com.ecoapi.techstore.order.domain.model.OrderId;

public class CapturePayPalPaymentService implements CapturePayPalPaymentUseCase {

    private final OrderRepositoryPort orderRepository;
    private final PaymentProviderPort paymentProvider;

    public CapturePayPalPaymentService(OrderRepositoryPort orderRepository,
                                       PaymentProviderPort paymentProvider) {
        this.orderRepository = orderRepository;
        this.paymentProvider = paymentProvider;
    }

    @Override
    public Order execute(CapturePayPalPaymentCommand command) {
        Order order = orderRepository.findById(OrderId.of(command.orderId()))
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + command.orderId()));

        if (!order.isPayPalPayment()) {
            throw new IllegalStateException("Order does not use PAYPAL as payment method");
        }
        if (order.getPaymentTransaction() == null) {
            throw new IllegalStateException("PayPal payment has not been initiated for this order");
        }
        if (order.getPaymentTransaction().isCaptured()) {
            return order;
        }

        String providerOrderId = order.getPaymentTransaction().providerOrderId();
        if (command.providerOrderId() != null
                && !command.providerOrderId().isBlank()
                && !providerOrderId.equals(command.providerOrderId().trim())) {
            throw new IllegalStateException("Provided provider order id does not match initiated PayPal order id");
        }

        try {
            PaymentProviderPort.CapturePayPalOrderResult captureResult =
                    paymentProvider.capturePayPalOrder(providerOrderId);

            if (!"COMPLETED".equalsIgnoreCase(captureResult.providerStatus())) {
                order.failPayPalPayment(providerOrderId,
                        "PayPal capture failed with status: " + captureResult.providerStatus());
                orderRepository.save(order);
                throw new PaymentProcessingException("PayPal payment was not completed");
            }

            order.capturePayPalPayment(providerOrderId, captureResult.providerCaptureId());
            return orderRepository.save(order);
        } catch (RuntimeException ex) {
            if (!order.getPaymentTransaction().isCaptured() && !order.getPaymentTransaction().isFailed()) {
                String reason = ex.getMessage() != null ? ex.getMessage() : "Unknown PayPal capture error";
                order.failPayPalPayment(providerOrderId, reason);
                orderRepository.save(order);
            }
            if (ex instanceof PaymentProcessingException) {
                throw ex;
            }
            throw new PaymentProcessingException("Failed to capture PayPal payment", ex);
        }
    }
}
