package com.ecoapi.techstore.order.infrastructure.scheduler;

import com.ecoapi.techstore.order.application.port.out.OrderRepositoryPort;
import com.ecoapi.techstore.order.application.port.out.StockReservationPort;
import com.ecoapi.techstore.order.domain.model.Order;
import com.ecoapi.techstore.order.domain.model.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AbandonedOrderCleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AbandonedOrderCleanupScheduler.class);

    private final OrderRepositoryPort orderRepository;
    private final StockReservationPort stockReservationPort;
    private final long expirationMinutes;

    public AbandonedOrderCleanupScheduler(
            OrderRepositoryPort orderRepository,
            StockReservationPort stockReservationPort,
            @Value("${app.order.expiration-minutes:30}") long expirationMinutes) {
        this.orderRepository = orderRepository;
        this.stockReservationPort = stockReservationPort;
        this.expirationMinutes = expirationMinutes;
    }

    @Scheduled(fixedRateString = "${app.order.cleanup-rate-ms:300000}")
    @Transactional
    public void cleanupAbandonedOrders() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(expirationMinutes);
        List<Order> expiredOrders = orderRepository.findPendingOrdersCreatedBefore(cutoff);

        if (expiredOrders.isEmpty()) {
            return;
        }

        logger.info("Found {} abandoned pending orders older than {} minutes. Restoring stock and cancelling.",
                expiredOrders.size(), expirationMinutes);

        for (Order order : expiredOrders) {
            try {
                for (OrderItem item : order.getOrderItems()) {
                    stockReservationPort.releaseStock(item.getProductId().value(), item.getQuantity());
                }
                order.cancel();
                orderRepository.save(order);
                logger.info("Successfully cancelled expired order ID: {} and released stock", order.getId().getValue());
            } catch (Exception ex) {
                logger.error("Failed to cancel expired order ID: {}: {}", order.getId().getValue(), ex.getMessage(), ex);
            }
        }
    }
}
