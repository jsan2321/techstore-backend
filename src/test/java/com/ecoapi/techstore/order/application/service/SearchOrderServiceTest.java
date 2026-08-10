package com.ecoapi.techstore.order.application.service;

import com.ecoapi.techstore.common.application.dto.PagedResult;
import com.ecoapi.techstore.order.application.port.out.OrderRepositoryPort;
import com.ecoapi.techstore.order.application.service.dto.SearchOrderCriteria;
import com.ecoapi.techstore.order.domain.model.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SearchOrderServiceTest {

    @Test
    void searchDelegatesToOrderRepositoryPort() {
        OrderRepositoryPort repositoryPort = mock(OrderRepositoryPort.class);
        SearchOrderCriteria criteria = SearchOrderCriteria.builder()
                .page(0)
                .size(10)
                .sortBy("orderDate")
                .sortDirection("desc")
                .build();

        Order order = mock(Order.class);
        PagedResult<Order> expectedResult = PagedResult.of(List.of(order), 0, 10, 1);

        when(repositoryPort.search(criteria)).thenReturn(expectedResult);

        SearchOrderService service = new SearchOrderService(repositoryPort);
        PagedResult<Order> result = service.search(criteria);

        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
        verify(repositoryPort, times(1)).search(criteria);
    }
}
