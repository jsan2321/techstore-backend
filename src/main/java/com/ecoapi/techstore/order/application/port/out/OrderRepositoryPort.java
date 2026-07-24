package com.ecoapi.techstore.order.application.port.out;

import com.ecoapi.techstore.common.application.dto.PagedResult;
import com.ecoapi.techstore.order.application.service.dto.SearchOrderCriteria;
import com.ecoapi.techstore.order.domain.model.Order;
import com.ecoapi.techstore.order.domain.model.OrderId;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(OrderId orderId);
    List<Order> findByUserId(UserId userId);
    List<Order> findAll();
    
    /**
     * Search orders with filters and pagination
     */
    PagedResult<Order> search(SearchOrderCriteria criteria);
    
    boolean existsById(OrderId orderId);
}
