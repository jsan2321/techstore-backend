package com.ecoapi.techstore.order.application.service;

import com.ecoapi.techstore.common.domain.valueobjects.Money;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;
import com.ecoapi.techstore.config.TestcontainersConfiguration;
import com.ecoapi.techstore.order.application.port.out.StockReservationPort;
import com.ecoapi.techstore.order.domain.exception.InsufficientStockException;
import com.ecoapi.techstore.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.techstore.product.domain.model.Brand;
import com.ecoapi.techstore.product.domain.model.BrandId;
import com.ecoapi.techstore.product.domain.model.Category;
import com.ecoapi.techstore.product.domain.model.CategoryId;
import com.ecoapi.techstore.product.domain.model.Product;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.entity.BrandEntity;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.entity.CategoryEntity;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.repository.JpaBrandRepository;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.repository.JpaCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class ConcurrentStockReservationTest {

    @Autowired
    private StockReservationPort stockReservationPort;

    @Autowired
    private ProductRepositoryPort productRepository;

    @Autowired
    private JpaBrandRepository brandRepository;

    @Autowired
    private JpaCategoryRepository categoryRepository;

    @Test
    @DisplayName("Should prevent overselling when two users purchase the last item concurrently")
    void shouldPreventOversellingUnderConcurrency() throws InterruptedException {
        String suffix = String.valueOf(System.currentTimeMillis());
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("Keychron-" + suffix);
        brandEntity = brandRepository.save(brandEntity);

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName("Keyboards-" + suffix);
        categoryEntity = categoryRepository.save(categoryEntity);

        Product product = Product.reconstitute(
                null,
                "Mechanical Switch Keyboard " + suffix,
                new Brand(BrandId.of(brandEntity.getId()), brandEntity.getName()),
                Money.of(new BigDecimal("149.99")),
                1,
                "Flagship peripheral",
                new Category(CategoryId.of(categoryEntity.getId()), categoryEntity.getName()),
                null,
                true,
                null,
                false,
                0L
        );
        Product saved = productRepository.save(product);
        Long productId = saved.getId().value();

        int numberOfThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(numberOfThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    stockReservationPort.reserveStock(productId, 1);
                    successCount.incrementAndGet();
                } catch (InsufficientStockException e) {
                    failureCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        finishLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(1);

        Product reloaded = productRepository.findById(ProductId.of(productId)).orElseThrow();
        assertThat(reloaded.getStock()).isEqualTo(0);
    }
}
