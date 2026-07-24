package com.ecoapi.techstore.order.application.service;

import com.ecoapi.techstore.common.application.dto.PagedResult;
import com.ecoapi.techstore.order.application.port.in.SearchOrderUseCase;
import com.ecoapi.techstore.order.application.port.out.OrderRepositoryPort;
import com.ecoapi.techstore.order.application.service.dto.SearchOrderCriteria;
import com.ecoapi.techstore.order.domain.model.Order;

/**
 * Application Service for searching orders
 * Implements the Query pattern for order search
 */
public class SearchOrderService implements SearchOrderUseCase {
    
    private final OrderRepositoryPort orderRepository;
    
    public SearchOrderService(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    @Override
    public PagedResult<Order> search(SearchOrderCriteria criteria) {
        return orderRepository.search(criteria);
    }
}