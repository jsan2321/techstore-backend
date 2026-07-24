package com.ecoapi.techstore.order.application.port.in;

import com.ecoapi.techstore.order.application.service.dto.InitiatePayPalPaymentCommand;
import com.ecoapi.techstore.order.application.service.dto.InitiatePayPalPaymentResult;

public interface InitiatePayPalPaymentUseCase {
    InitiatePayPalPaymentResult execute(InitiatePayPalPaymentCommand command);
}
