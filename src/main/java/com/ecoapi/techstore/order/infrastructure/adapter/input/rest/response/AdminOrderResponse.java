package com.ecoapi.techstore.order.infrastructure.adapter.input.rest.response;

import com.ecoapi.techstore.order.application.port.out.dto.UserSummaryData;
import com.ecoapi.techstore.order.domain.model.Order;
import com.ecoapi.techstore.order.domain.model.PaymentStatus;
import com.ecoapi.techstore.order.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Admin-specific order response that includes user summary information.
 */
public record AdminOrderResponse(
        Long id,
        Long userId,
        UserSummaryResponse user,
        List<OrderItemResponse> orderItems,
        BigDecimal totalAmount,
        OrderStatus status,
        LocalDateTime orderDate,
        LocalDate estimatedDelivery,
        int totalItems,
        OrderResponse.ShippingAddressResponse shippingAddress,
        String paymentMethod,
        PaymentStatus paymentStatus,
        String paymentProviderOrderId,
        String paymentProviderCaptureId,
        String paymentFailureReason
) {

    public static AdminOrderResponse fromDomain(Order order, UserSummaryData userSummary) {
        OrderResponse base = OrderResponse.fromDomain(order);

        UserSummaryResponse userResponse = null;
        if (userSummary != null) {
            userResponse = new UserSummaryResponse(
                    userSummary.userId(),
                    userSummary.firstName(),
                    userSummary.lastName(),
                    userSummary.email(),
                    userSummary.status(),
                    userSummary.emailVerified()
            );
        }

        return new AdminOrderResponse(
                base.id(),
                base.userId(),
                userResponse,
                base.orderItems(),
                base.totalAmount(),
                base.status(),
                base.orderDate(),
                base.estimatedDelivery(),
                base.totalItems(),
                base.shippingAddress(),
                                base.paymentMethod(),
                                base.paymentStatus(),
                                base.paymentProviderOrderId(),
                                base.paymentProviderCaptureId(),
                                base.paymentFailureReason()
        );
    }

    public record UserSummaryResponse(
            Long id,
            String firstName,
            String lastName,
            String email,
            String status,
            boolean emailVerified
    ) {
    }
}
