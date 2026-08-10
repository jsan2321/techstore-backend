package com.ecoapi.techstore.order.infrastructure.adapter.output.persistence;

import com.ecoapi.techstore.config.TestcontainersConfiguration;
import com.ecoapi.techstore.order.domain.model.OrderStatus;
import com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.entity.OrderEntity;
import com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.entity.ShippingAddressEmbeddable;
import com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.repository.JpaOrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class OrderRepositoryTest {

    @Autowired
    private JpaOrderRepository repository;

    @Test
    void savesOrderAndFindsByUserId() {
        OrderEntity order = new OrderEntity();
        order.setUserId(42L);
        order.setTotalAmount(new BigDecimal("150.00"));
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setPaymentMethod("PAYPAL");

        ShippingAddressEmbeddable shippingAddress = new ShippingAddressEmbeddable(
                "Jane Doe",
                "123 Main St",
                "Apt 4B",
                "Tech City",
                "CA",
                "90210",
                "US",
                "Leave at door"
        );
        order.setShippingAddress(shippingAddress);

        repository.save(order);

        List<OrderEntity> orders = repository.findByUserId(42L);
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getTotalAmount()).isEqualByComparingTo("150.00");
        assertThat(orders.get(0).getShippingAddress().getFullName()).isEqualTo("Jane Doe");
        assertThat(repository.existsByUserId(42L)).isTrue();
        assertThat(repository.existsByUserId(999L)).isFalse();
    }
}
