package com.ecoapi.techstore.order.application.port.in;

import com.ecoapi.techstore.order.application.service.dto.CapturePayPalPaymentCommand;
import com.ecoapi.techstore.order.domain.model.Order;

public interface CapturePayPalPaymentUseCase {
    Order execute(CapturePayPalPaymentCommand command);
}
