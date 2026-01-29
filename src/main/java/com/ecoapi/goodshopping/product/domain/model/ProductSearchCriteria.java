package com.ecoapi.goodshopping.product.domain.model;

import java.math.BigDecimal;

/**
 * Search criteria for filtering products
 * Value object encapsulating search parameters
 */
public record ProductSearchCriteria(
    String category,
    String brand,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    Boolean inStock
) {
    
    public boolean isEmpty() {
        return category == null 
            && brand == null 
            && minPrice == null 
            && maxPrice == null 
            && inStock == null;
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
        
        public ProductSearchCriteria build() {
            return new ProductSearchCriteria(category, brand, minPrice, maxPrice, inStock);
        }
    }
}
