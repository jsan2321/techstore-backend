package com.ecoapi.techstore.order.application.service.dto;

import com.ecoapi.techstore.order.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Criteria for searching and filtering orders
 * Used by admin to find orders based on various filters
 */
public record SearchOrderCriteria(
    Long userId,
    OrderStatus status,
    LocalDateTime startDate,
    LocalDateTime endDate,
    BigDecimal minAmount,
    BigDecimal maxAmount,
    int page,
    int size,
    String sortBy,
    String sortDirection
) {
    public SearchOrderCriteria {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
        if (size > 100) size = 100;
        if (sortBy == null || sortBy.isBlank()) sortBy = "orderDate";
        if (sortDirection == null || sortDirection.isBlank()) sortDirection = "desc";
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Long userId;
        private OrderStatus status;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private BigDecimal minAmount;
        private BigDecimal maxAmount;
        private int page = 0;
        private int size = 20;
        private String sortBy = "orderDate";
        private String sortDirection = "desc";
        
        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }
        
        public Builder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }
        
        public Builder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }
        
        public Builder minAmount(BigDecimal minAmount) {
            this.minAmount = minAmount;
            return this;
        }
        
        public Builder maxAmount(BigDecimal maxAmount) {
            this.maxAmount = maxAmount;
            return this;
        }
        
        public Builder page(int page) {
            this.page = page;
            return this;
        }
        
        public Builder size(int size) {
            this.size = size;
            return this;
        }
        
        public Builder sortBy(String sortBy) {
            this.sortBy = sortBy;
            return this;
        }
        
        public Builder sortDirection(String sortDirection) {
            this.sortDirection = sortDirection;
            return this;
        }
        
        public SearchOrderCriteria build() {
            return new SearchOrderCriteria(
                userId, status, startDate, endDate, 
                minAmount, maxAmount, page, size, sortBy, sortDirection
            );
        }
    }
}