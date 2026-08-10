package com.ecoapi.techstore.product.application.service;

import com.ecoapi.techstore.product.application.port.out.BrandRepositoryPort;
import com.ecoapi.techstore.product.application.port.out.CategoryRepositoryPort;
import com.ecoapi.techstore.product.application.port.out.ProductEventPublisherPort;
import com.ecoapi.techstore.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.techstore.product.application.service.dto.ProductCommand;
import com.ecoapi.techstore.product.domain.events.ProductCreatedEvent;
import com.ecoapi.techstore.product.domain.exception.ProductAlreadyExistsException;
import com.ecoapi.techstore.product.domain.model.Brand;
import com.ecoapi.techstore.product.domain.model.BrandId;
import com.ecoapi.techstore.product.domain.model.Category;
import com.ecoapi.techstore.product.domain.model.CategoryId;
import com.ecoapi.techstore.product.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AddProductServiceTest {

    @Test
    void addsProductSuccessfullyAndPublishesEvent() {
        ProductRepositoryPort productRepo = Mockito.mock(ProductRepositoryPort.class);
        CategoryRepositoryPort categoryRepo = Mockito.mock(CategoryRepositoryPort.class);
        BrandRepositoryPort brandRepo = Mockito.mock(BrandRepositoryPort.class);
        ProductEventPublisherPort eventPublisher = Mockito.mock(ProductEventPublisherPort.class);

        when(productRepo.existsByName("Smart Watch")).thenReturn(false);
        when(brandRepo.findById(BrandId.of(1L))).thenReturn(Optional.of(new Brand(BrandId.of(1L), "TechBrand")));
        when(categoryRepo.findById(CategoryId.of(2L))).thenReturn(Optional.of(new Category(CategoryId.of(2L), "Wearables")));
        when(productRepo.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        AddProductService service = new AddProductService(productRepo, categoryRepo, brandRepo, eventPublisher);

        ProductCommand command = new ProductCommand(
                "Smart Watch",
                1L,
                new BigDecimal("199.99"),
                50,
                "Waterproof smart watch",
                2L,
                false,
                null,
                true
        );

        Product saved = service.addProduct(command);

        assertThat(saved.getName()).isEqualTo("Smart Watch");
        assertThat(saved.getStock()).isEqualTo(50);
        assertThat(saved.isFeatured()).isTrue();

        verify(productRepo).save(any(Product.class));
        verify(eventPublisher).publish(any(ProductCreatedEvent.class));
    }

    @Test
    void throwsExceptionWhenProductAlreadyExists() {
        ProductRepositoryPort productRepo = Mockito.mock(ProductRepositoryPort.class);
        CategoryRepositoryPort categoryRepo = Mockito.mock(CategoryRepositoryPort.class);
        BrandRepositoryPort brandRepo = Mockito.mock(BrandRepositoryPort.class);
        ProductEventPublisherPort eventPublisher = Mockito.mock(ProductEventPublisherPort.class);

        when(productRepo.existsByName("Smart Watch")).thenReturn(true);

        AddProductService service = new AddProductService(productRepo, categoryRepo, brandRepo, eventPublisher);

        ProductCommand command = new ProductCommand(
                "Smart Watch",
                1L,
                new BigDecimal("199.99"),
                50,
                "Waterproof smart watch",
                2L,
                false,
                null,
                false
        );

        assertThatThrownBy(() -> service.addProduct(command))
                .isInstanceOf(ProductAlreadyExistsException.class);
    }
}
