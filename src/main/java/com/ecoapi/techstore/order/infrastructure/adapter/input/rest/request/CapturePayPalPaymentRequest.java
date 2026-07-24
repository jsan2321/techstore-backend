package com.ecoapi.techstore.order.infrastructure.adapter.input.rest.request;

/**
 * Optional payload for PayPal capture endpoint.
 * providerOrderId is optional and can be sent by clients after redirect validation.
 */
public record CapturePayPalPaymentRequest(String providerOrderId) {
}
