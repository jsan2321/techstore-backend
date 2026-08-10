package com.ecoapi.techstore.order.domain.model;

import com.ecoapi.techstore.common.domain.valueobjects.Money;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.order.domain.exception.InvalidOrderStatusTransitionException;
import com.ecoapi.techstore.order.domain.valueobjects.ShippingAddress;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    private ShippingAddress createSampleAddress() {
        return ShippingAddress.of("Jane Doe", "123 Main St", null, "Tech City", "CA", "90210", "US", null);
    }

    private OrderItem createSampleItem() {
        return new OrderItem(ProductId.of(1L), "Sample Product", "Sample Description", "https://example.com/img.png", 2, Money.of(new BigDecimal("25.00")));
    }

    @Test
    void placesOrderInPendingStatusWithCorrectTotal() {
        UserId userId = UserId.of(5L);
        List<OrderItem> items = List.of(createSampleItem());
        ShippingAddress address = createSampleAddress();

        Order order = Order.place(userId, items, address, "CREDIT_CARD");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getTotalAmount()).isEqualTo(Money.of(new BigDecimal("50.00")));
        assertThat(order.getShippingAddress().fullName()).isEqualTo("Jane Doe");
        assertThat(order.isPending()).isTrue();
    }

    @Test
    void allowsValidStatusTransitions() {
        Order order = Order.place(UserId.of(5L), List.of(createSampleItem()), createSampleAddress(), "CREDIT_CARD");

        order.process();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PROCESSING);

        order.ship();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);

        order.deliver();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        assertThat(order.isDelivered()).isTrue();
    }

    @Test
    void throwsExceptionOnInvalidStatusTransition() {
        Order order = Order.place(UserId.of(5L), List.of(createSampleItem()), createSampleAddress(), "CREDIT_CARD");

        assertThatThrownBy(order::deliver)
                .isInstanceOf(InvalidOrderStatusTransitionException.class);
    }
}
