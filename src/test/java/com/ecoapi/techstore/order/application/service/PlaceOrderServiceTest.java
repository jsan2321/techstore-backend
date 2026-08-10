package com.ecoapi.techstore.order.application.service;

import com.ecoapi.techstore.order.application.port.out.*;
import com.ecoapi.techstore.order.application.port.out.dto.*;
import com.ecoapi.techstore.order.application.service.dto.PlaceOrderCommand;
import com.ecoapi.techstore.order.domain.exception.InsufficientStockException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PlaceOrderServiceTest {
    @Test
    void releasesPreviouslyReservedStockWhenALaterCartItemIsUnavailable() {
        OrderRepositoryPort orders = mock(OrderRepositoryPort.class);
        CartAccessPort carts = mock(CartAccessPort.class);
        StockReservationPort stock = mock(StockReservationPort.class);
        UserValidationPort users = mock(UserValidationPort.class);
        UserAddressPort addresses = mock(UserAddressPort.class);
        OrderEventPublisherPort events = mock(OrderEventPublisherPort.class);
        var first = new CartItemData(1L, "First", null, null, 1, BigDecimal.TEN);
        var second = new CartItemData(2L, "Second", null, null, 1, BigDecimal.TEN);
        when(users.isValidUser(7L)).thenReturn(true);
        when(carts.getCartForUser(7L)).thenReturn(new CartData(9L, 7L, List.of(first, second)));
        when(stock.hasAvailableStock(1L, 1)).thenReturn(true);
        when(stock.hasAvailableStock(2L, 1)).thenReturn(false);
        when(stock.getProductName(2L)).thenReturn("Second");
        var service = new PlaceOrderService(orders, carts, stock, users, addresses, events);

        assertThatThrownBy(() -> service.execute(PlaceOrderCommand.withAddress(7L, "Customer", "Street", null, "City", "State", "12345", "US", null, "PAYPAL")))
                .isInstanceOf(InsufficientStockException.class);

        verify(stock).reserveStock(1L, 1);
        verify(stock).releaseStock(1L, 1);
        verify(orders, never()).save(any());
        verify(carts, never()).clearCart(any());
    }
}
