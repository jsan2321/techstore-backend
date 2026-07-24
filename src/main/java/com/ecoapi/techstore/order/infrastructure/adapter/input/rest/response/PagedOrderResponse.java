package com.ecoapi.techstore.order.infrastructure.adapter.input.rest.response;

import com.ecoapi.techstore.common.application.dto.PagedResult;
import com.ecoapi.techstore.order.domain.model.Order;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Paginated response for orders
 */
public record PagedOrderResponse(
    List<OrderResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {
    public static PagedOrderResponse fromPagedResult(PagedResult<Order> result) {
        List<OrderResponse> content = result.content().stream()
                .map(OrderResponse::fromDomain)
                .collect(Collectors.toList());
        return new PagedOrderResponse(
            content,
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages(),
            result.first(),
            result.last()
        );
    }
}