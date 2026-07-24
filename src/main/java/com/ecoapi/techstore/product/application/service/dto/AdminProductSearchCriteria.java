package com.ecoapi.techstore.product.application.service.dto;

import java.math.BigDecimal;

/**
 * Search criteria for admin product searches.
 * Extends public search capabilities with admin-specific filters.
 */
public record AdminProductSearchCriteria(
    String category,
    String brand,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    Boolean inStock,
    Boolean active,
    Boolean featured,
    String nameContains,
    int page,
    int size,
    String sortBy,
    String sortDirection
) {

    public AdminProductSearchCriteria {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
        if (size > 100) size = 100;
        if (sortBy == null || sortBy.isBlank()) sortBy = "name";
        if (sortDirection == null || sortDirection.isBlank()) sortDirection = "asc";
    }

    public boolean isEmpty() {
        return category == null
            && brand == null
            && minPrice == null
            && maxPrice == null
            && inStock == null
            && active == null
            && featured == null
            && nameContains == null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String category;
        private String brand;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private Boolean inStock;
        private Boolean active;
        private Boolean featured;
        private String nameContains;
        private int page = 0;
        private int size = 20;
        private String sortBy = "name";
        private String sortDirection = "asc";

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder brand(String brand) {
            this.brand = brand;
            return this;
        }

        public Builder minPrice(BigDecimal minPrice) {
            this.minPrice = minPrice;
            return this;
        }

        public Builder maxPrice(BigDecimal maxPrice) {
            this.maxPrice = maxPrice;
            return this;
        }

        public Builder inStock(Boolean inStock) {
            this.inStock = inStock;
            return this;
        }

        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        public Builder featured(Boolean featured) {
            this.featured = featured;
            return this;
        }

        public Builder nameContains(String nameContains) {
            this.nameContains = nameContains;
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

        public AdminProductSearchCriteria build() {
            return new AdminProductSearchCriteria(
                category, brand, minPrice, maxPrice, inStock, active, featured, nameContains,
                page, size, sortBy, sortDirection
            );
        }
    }
}
