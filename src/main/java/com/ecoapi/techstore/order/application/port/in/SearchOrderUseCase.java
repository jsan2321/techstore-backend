package com.ecoapi.techstore.order.application.port.in;

import com.ecoapi.techstore.common.application.dto.PagedResult;
import com.ecoapi.techstore.order.application.service.dto.SearchOrderCriteria;
import com.ecoapi.techstore.order.domain.model.Order;

/**
 * Input Port (Use Case) for searching orders with filters and pagination
 */
public interface SearchOrderUseCase {
    
    /**
     * Search orders based on criteria
     * @param criteria Search filters and pagination
     * @return Paginated list of orders
     */
    PagedResult<Order> search(SearchOrderCriteria criteria);
}