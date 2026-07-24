package com.ecoapi.techstore.order.domain.valueobjects;

import com.ecoapi.techstore.order.domain.model.PaymentStatus;

import java.time.LocalDateTime;

/**
 * Immutable value object that captures provider payment transaction state.
 */
public record PaymentTransaction(
        String provider,
        String providerOrderId,
        String providerCaptureId,
        PaymentStatus status,
        String failureReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public PaymentTransaction {
        if (provider == null || provider.isBlank()) {
            throw new IllegalArgumentException("Payment provider is required");
        }
        if (providerOrderId == null || providerOrderId.isBlank()) {
            throw new IllegalArgumentException("Provider order id is required");
        }
        if (status == null) {
            throw new IllegalArgumentException("Payment status is required");
        }
        if (createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("Payment timestamps are required");
        }
        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("Payment updatedAt cannot be before createdAt");
        }

        provider = provider.trim().toUpperCase();
        providerOrderId = providerOrderId.trim();
        providerCaptureId = providerCaptureId != null ? providerCaptureId.trim() : null;
        failureReason = failureReason != null ? failureReason.trim() : null;

        if (status == PaymentStatus.CAPTURED && (providerCaptureId == null || providerCaptureId.isBlank())) {
            throw new IllegalArgumentException("Captured payment requires a provider capture id");
        }
        if (status == PaymentStatus.FAILED && (failureReason == null || failureReason.isBlank())) {
            throw new IllegalArgumentException("Failed payment requires a failure reason");
        }
    }

    public static PaymentTransaction initiated(String provider, String providerOrderId, LocalDateTime now) {
        return new PaymentTransaction(provider, providerOrderId, null, PaymentStatus.INITIATED, null, now, now);
    }

    public PaymentTransaction captured(String captureId, LocalDateTime now) {
        if (!status.canTransitionTo(PaymentStatus.CAPTURED)) {
            throw new IllegalStateException("Payment cannot transition from " + status + " to CAPTURED");
        }
        return new PaymentTransaction(provider, providerOrderId, captureId, PaymentStatus.CAPTURED, null, createdAt, now);
    }

    public PaymentTransaction failed(String reason, LocalDateTime now) {
        if (!status.canTransitionTo(PaymentStatus.FAILED)) {
            throw new IllegalStateException("Payment cannot transition from " + status + " to FAILED");
        }
        return new PaymentTransaction(provider, providerOrderId, providerCaptureId, PaymentStatus.FAILED, reason, createdAt, now);
    }

    public boolean isCaptured() {
        return status == PaymentStatus.CAPTURED;
    }

    public boolean isFailed() {
        return status == PaymentStatus.FAILED;
    }
}
