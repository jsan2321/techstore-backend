package com.ecoapi.techstore.user.infrastructure.adapter.output.order;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.repository.JpaOrderRepository;
import com.ecoapi.techstore.user.application.port.out.OrderLookupPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adapter for querying order data needed by user lifecycle use cases.
 */
@Component
public class OrderLookupAdapter implements OrderLookupPort {

    private static final Logger logger = LoggerFactory.getLogger(OrderLookupAdapter.class);

    private final JpaOrderRepository jpaOrderRepository;

    public OrderLookupAdapter(JpaOrderRepository jpaOrderRepository) {
        this.jpaOrderRepository = jpaOrderRepository;
    }

    @Override
    public boolean hasOrders(UserId userId) {
        try {
            return jpaOrderRepository.existsByUserId(userId.value());
        } catch (Exception e) {
            logger.error("Error checking orders for user {}: {}", userId.value(), e.getMessage());
            throw e;
        }
    }
}
