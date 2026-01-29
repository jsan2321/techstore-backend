package com.ecoapi.goodshopping.product.domain.model;

import java.util.Objects;

/**
 * Value Object representing Brand identifier
 */
public class BrandId {
    
    private final Long value;
    
    private BrandId(Long value) {
        this.value = value;
    }
    
    public static BrandId of(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Brand ID must be a positive number");
        }
        return new BrandId(value);
    }
    
    public Long value() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrandId brandId = (BrandId) o;
        return Objects.equals(value, brandId.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return "BrandId{" + value + '}';
    }
}
