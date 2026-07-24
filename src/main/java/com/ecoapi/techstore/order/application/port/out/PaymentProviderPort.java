package com.ecoapi.techstore.order.application.port.out;

import java.math.BigDecimal;

public interface PaymentProviderPort {

    default CreatePayPalOrderResult createPayPalOrder(Long orderId, BigDecimal amount, String currency) {
        return createPayPalOrder(orderId, amount, currency, null);
    }

    CreatePayPalOrderResult createPayPalOrder(Long orderId,
                                              BigDecimal amount,
                                              String currency,
                                              PayPalShippingAddress shippingAddress);

    CapturePayPalOrderResult capturePayPalOrder(String providerOrderId);

    record CreatePayPalOrderResult(String providerOrderId, String approvalUrl) {
    }

    record CapturePayPalOrderResult(String providerOrderId, String providerCaptureId, String providerStatus) {
    }

    record PayPalShippingAddress(
            String fullName,
            String street,
            String addressLine2,
            String city,
            String state,
            String postalCode,
            String countryCode
    ) {
    }
}
