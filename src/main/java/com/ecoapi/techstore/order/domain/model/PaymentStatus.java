package com.ecoapi.techstore.order.domain.model;

public enum PaymentStatus {
    INITIATED,
    CAPTURED,
    FAILED;

    public boolean canTransitionTo(PaymentStatus newStatus) {
        return switch (this) {
            case INITIATED -> newStatus == CAPTURED || newStatus == FAILED;
            case CAPTURED, FAILED -> false;
        };
    }
}
