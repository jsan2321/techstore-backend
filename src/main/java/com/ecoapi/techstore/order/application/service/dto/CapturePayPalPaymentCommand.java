package com.ecoapi.techstore.order.application.service.dto;

public record CapturePayPalPaymentCommand(
        Long orderId,
        String providerOrderId
) {
    public CapturePayPalPaymentCommand {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Order id must be a positive number");
        }
    }

    public static CapturePayPalPaymentCommand of(Long orderId, String providerOrderId) {
        return new CapturePayPalPaymentCommand(orderId, providerOrderId);
    }
}
