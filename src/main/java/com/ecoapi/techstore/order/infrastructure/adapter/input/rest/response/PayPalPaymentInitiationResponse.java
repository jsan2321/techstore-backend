package com.ecoapi.techstore.order.infrastructure.adapter.input.rest.response;

import com.ecoapi.techstore.order.application.service.dto.InitiatePayPalPaymentResult;

public record PayPalPaymentInitiationResponse(
        Long orderId,
        String providerOrderId,
        String approvalUrl
) {
    public static PayPalPaymentInitiationResponse fromResult(InitiatePayPalPaymentResult result) {
        return new PayPalPaymentInitiationResponse(result.orderId(), result.providerOrderId(), result.approvalUrl());
    }
}
