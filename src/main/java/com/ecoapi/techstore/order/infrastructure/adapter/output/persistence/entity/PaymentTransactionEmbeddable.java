package com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.entity;

import com.ecoapi.techstore.order.domain.model.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;

@Embeddable
public class PaymentTransactionEmbeddable {

    @Column(name = "payment_provider", length = 30)
    private String provider;

    @Column(name = "payment_provider_order_id", length = 120)
    private String providerOrderId;

    @Column(name = "payment_provider_capture_id", length = 120)
    private String providerCaptureId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 30)
    private PaymentStatus status;

    @Column(name = "payment_failure_reason", length = 500)
    private String failureReason;

    @Column(name = "payment_created_at")
    private LocalDateTime createdAt;

    @Column(name = "payment_updated_at")
    private LocalDateTime updatedAt;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderOrderId() {
        return providerOrderId;
    }

    public void setProviderOrderId(String providerOrderId) {
        this.providerOrderId = providerOrderId;
    }

    public String getProviderCaptureId() {
        return providerCaptureId;
    }

    public void setProviderCaptureId(String providerCaptureId) {
        this.providerCaptureId = providerCaptureId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
