package com.ecoapi.techstore.order.domain.model;

import java.util.Objects;

public class OrderItemId {
    
    private final Long value;
    
    private OrderItemId(Long value) {
        this.value = value;
    }
    
    public static OrderItemId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("OrderItemId cannot be null");
        }
        return new OrderItemId(value);
    }
    
    public Long getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemId that = (OrderItemId) o;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return "OrderItemId{" + value + '}';
    }
}
