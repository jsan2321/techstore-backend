package com.ecoapi.techstore.order.infrastructure.adapter.input.rest.response;

import com.ecoapi.techstore.common.application.dto.PagedResult;
import com.ecoapi.techstore.order.domain.model.Order;

import java.util.List;

/**
 * Paginated response for admin order search.
 */
public record AdminPagedOrderResponse(
        List<AdminOrderResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public static AdminPagedOrderResponse fromPagedResult(PagedResult<Order> result,
                                                           List<AdminOrderResponse> content) {
        return new AdminPagedOrderResponse(
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
