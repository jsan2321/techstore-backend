package com.ecoapi.techstore.order.infrastructure.adapter.input.rest.response;

import com.ecoapi.techstore.order.domain.model.Order;
import com.ecoapi.techstore.order.domain.model.PaymentStatus;
import com.ecoapi.techstore.order.domain.model.OrderStatus;
import com.ecoapi.techstore.order.domain.valueobjects.PaymentTransaction;
import com.ecoapi.techstore.order.domain.valueobjects.ShippingAddress;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record OrderResponse(
    Long id,
    Long userId,
    List<OrderItemResponse> orderItems,
    BigDecimal totalAmount,
    OrderStatus status,
    LocalDateTime orderDate,
    LocalDate estimatedDelivery,
    int totalItems,
    ShippingAddressResponse shippingAddress,
    String paymentMethod,
    PaymentStatus paymentStatus,
    String paymentProviderOrderId,
    String paymentProviderCaptureId,
    String paymentFailureReason
) {
    public static OrderResponse fromDomain(Order order) {
        ShippingAddressResponse addressResponse = null;
        if (order.getShippingAddress() != null) {
            ShippingAddress addr = order.getShippingAddress();
            addressResponse = new ShippingAddressResponse(
                    addr.fullName(),
                    addr.street(),
                    addr.addressLine2(),
                    addr.city(),
                    addr.state(),
                    addr.postalCode(),
                    addr.country(),
                    addr.deliveryNotes()
            );
        }

        PaymentTransaction paymentTransaction = order.getPaymentTransaction();
        
        return new OrderResponse(
                order.getId() != null ? order.getId().getValue() : null,
                order.getUserId().value(),
                order.getOrderItems().stream()
                        .map(OrderItemResponse::fromDomain)
                        .collect(Collectors.toList()),
                order.getTotalAmount().value(),
                order.getStatus(),
                order.getOrderDate(),
                calculateEstimatedDelivery(order),
                order.getTotalItems(),
                addressResponse,
                order.getPaymentMethod(),
                paymentTransaction != null ? paymentTransaction.status() : null,
                paymentTransaction != null ? paymentTransaction.providerOrderId() : null,
                paymentTransaction != null ? paymentTransaction.providerCaptureId() : null,
                paymentTransaction != null ? paymentTransaction.failureReason() : null
        );
    }

    private static LocalDate calculateEstimatedDelivery(Order order) {
        if (order.getOrderDate() == null || order.getStatus() == null) {
            return null;
        }

        return switch (order.getStatus()) {
            case PENDING, PROCESSING -> order.getOrderDate().toLocalDate().plusDays(3);
            case SHIPPED -> order.getOrderDate().toLocalDate().plusDays(1);
            case DELIVERED, CANCELLED -> null;
        };
    }
    
    /**
     * Nested record for shipping address in response
     */
    public record ShippingAddressResponse(
        String fullName,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        String country,
        String deliveryNotes
    ) {}
}
