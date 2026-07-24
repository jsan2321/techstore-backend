package com.ecoapi.techstore.order.application.service.dto;

public record InitiatePayPalPaymentResult(
        Long orderId,
        String providerOrderId,
        String approvalUrl
) {
}
