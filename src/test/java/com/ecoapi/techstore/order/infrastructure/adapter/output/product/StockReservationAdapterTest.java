package com.ecoapi.techstore.order.infrastructure.adapter.output.product;

import com.ecoapi.techstore.common.domain.valueobjects.Money;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;
import com.ecoapi.techstore.order.domain.exception.InsufficientStockException;
import com.ecoapi.techstore.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.techstore.product.domain.model.Brand;
import com.ecoapi.techstore.product.domain.model.Category;
import com.ecoapi.techstore.product.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StockReservationAdapterTest {

    private ProductRepositoryPort productRepository;
    private StockReservationAdapter adapter;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepositoryPort.class);
        adapter = new StockReservationAdapter(productRepository);
    }

    private Product createProduct(Long id, int stock) {
        return Product.reconstitute(
                ProductId.of(id),
                "Keychron Q1",
                new Brand("Keychron"),
                Money.of(new BigDecimal("199.99")),
                stock,
                "Custom mechanical keyboard",
                new Category("Keyboards"),
                null,
                true,
                null,
                false,
                0L
        );
    }

    @Test
    void reservesStockSuccessfullyWhenAvailable() {
        Long productId = 1L;
        Product product = createProduct(productId, 5);

        when(productRepository.findById(ProductId.of(productId))).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        adapter.reserveStock(productId, 2);

        assertThat(product.getStock()).isEqualTo(3);
        verify(productRepository).save(product);
    }

    @Test
    void throwsInsufficientStockExceptionWhenOptimisticLockingFails() {
        Long productId = 2L;
        Product product = createProduct(productId, 1);

        when(productRepository.findById(ProductId.of(productId))).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class)))
                .thenThrow(new OptimisticLockingFailureException("Row was updated by another transaction"));

        assertThatThrownBy(() -> adapter.reserveStock(productId, 1))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("due to concurrent purchase");
    }

    @Test
    void throwsInsufficientStockExceptionWhenStockIsInsufficient() {
        Long productId = 3L;
        Product product = createProduct(productId, 1);

        when(productRepository.findById(ProductId.of(productId))).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> adapter.reserveStock(productId, 2))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Insufficient stock");

        verify(productRepository, never()).save(any());
    }

    @Test
    void releasesStockSuccessfully() {
        Long productId = 4L;
        Product product = createProduct(productId, 2);

        when(productRepository.findById(ProductId.of(productId))).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        adapter.releaseStock(productId, 3);

        assertThat(product.getStock()).isEqualTo(5);
        verify(productRepository).save(product);
    }
}
