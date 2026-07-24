package com.ecoapi.techstore.order.domain.model;

import java.util.Objects;

public class OrderId {
    
    private final Long value;
    
    private OrderId(Long value) {
        this.value = value;
    }
    
    public static OrderId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("OrderId cannot be null");
        }
        return new OrderId(value);
    }
    
    public Long getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderId orderId = (OrderId) o;
        return Objects.equals(value, orderId.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return "OrderId{" + value + '}';
    }
}
