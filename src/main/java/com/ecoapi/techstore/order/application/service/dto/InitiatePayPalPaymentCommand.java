package com.ecoapi.techstore.order.application.service.dto;

public record InitiatePayPalPaymentCommand(Long orderId) {
    public InitiatePayPalPaymentCommand {
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("Order id must be a positive number");
        }
    }

    public static InitiatePayPalPaymentCommand of(Long orderId) {
        return new InitiatePayPalPaymentCommand(orderId);
    }
}
