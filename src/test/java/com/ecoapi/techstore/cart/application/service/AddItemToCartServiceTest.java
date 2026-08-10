package com.ecoapi.techstore.cart.application.service;

import com.ecoapi.techstore.cart.application.port.out.CartEventPublisherPort;
import com.ecoapi.techstore.cart.application.port.out.CartRepositoryPort;
import com.ecoapi.techstore.cart.application.port.out.ProductAccessPort;
import com.ecoapi.techstore.cart.application.port.out.dto.ProductData;
import com.ecoapi.techstore.cart.application.service.dto.AddItemCommand;
import com.ecoapi.techstore.cart.domain.events.ItemAddedToCartEvent;
import com.ecoapi.techstore.cart.domain.exception.ProductNotFoundException;
import com.ecoapi.techstore.cart.domain.model.Cart;
import com.ecoapi.techstore.cart.domain.model.CartId;
import com.ecoapi.techstore.common.domain.valueobjects.Money;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AddItemToCartServiceTest {

    @Test
    void addsItemToExistingUserCartAndPublishesEvent() {
        CartRepositoryPort cartRepo = Mockito.mock(CartRepositoryPort.class);
        ProductAccessPort productAccess = Mockito.mock(ProductAccessPort.class);
        CartEventPublisherPort eventPublisher = Mockito.mock(CartEventPublisherPort.class);

        Cart existingCart = Cart.reconstitute(CartId.of(100L), UserId.of(1L), new ArrayList<>(), Money.zero());
        when(cartRepo.findByUserId(UserId.of(1L))).thenReturn(Optional.of(existingCart));
        when(cartRepo.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));

        Money price = Money.of(new BigDecimal("25.00"));
        ProductData productData = new ProductData(
                10L,
                "Wireless Mouse",
                "Ergonomic Mouse",
                "https://example.com/mouse.png",
                price,
                price,
                null,
                20,
                true
        );
        when(productAccess.getProductById(10L)).thenReturn(Optional.of(productData));

        AddItemToCartService service = new AddItemToCartService(cartRepo, productAccess, eventPublisher);
        AddItemCommand command = AddItemCommand.forUser(1L, 10L, 2);

        Cart updatedCart = service.addItem(command);

        assertThat(updatedCart.getItemCount()).isEqualTo(1);
        assertThat(updatedCart.getTotalItems()).isEqualTo(2);
        assertThat(updatedCart.getTotalAmount()).isEqualTo(Money.of(new BigDecimal("50.00")));

        verify(cartRepo).save(existingCart);
        verify(eventPublisher).publish(any(ItemAddedToCartEvent.class));
    }

    @Test
    void throwsExceptionWhenProductNotFound() {
        CartRepositoryPort cartRepo = Mockito.mock(CartRepositoryPort.class);
        ProductAccessPort productAccess = Mockito.mock(ProductAccessPort.class);
        CartEventPublisherPort eventPublisher = Mockito.mock(CartEventPublisherPort.class);

        Cart existingCart = Cart.reconstitute(CartId.of(100L), UserId.of(1L), new ArrayList<>(), Money.zero());
        when(cartRepo.findByUserId(UserId.of(1L))).thenReturn(Optional.of(existingCart));
        when(productAccess.getProductById(99L)).thenReturn(Optional.empty());

        AddItemToCartService service = new AddItemToCartService(cartRepo, productAccess, eventPublisher);
        AddItemCommand command = AddItemCommand.forUser(1L, 99L, 1);

        assertThatThrownBy(() -> service.addItem(command))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found with id: 99");
    }
}
