package com.ecoapi.techstore.cart.domain.model;

import com.ecoapi.techstore.common.domain.valueobjects.Money;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class CartTest {

    @Test
    void shouldCreateEmptyCartForUser() {
        // 1. Arrange
        UserId userId = new UserId(1L);

        // 2. Act
        Cart cart = Cart.createFor(userId);

        // 3. Assert
        assertNotNull(cart);
        assertEquals(userId, cart.getUserId());
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getItemCount());
        assertEquals(Money.zero(), cart.getTotalAmount());
    }

    @Test
    void shouldAddNewItemAndRecalculateTotal() {
        // 1. Arrange
        UserId userId = new UserId(1L);
        Cart cart = Cart.createFor(userId);

        ProductId productId = new ProductId(100L);

        Money unitPrice = Money.of(new BigDecimal("50.00"));

        CartItem item = new CartItem(
                productId,
                "Development Laptop",        // productName
                "Laptop with Linux pre-installed",  // productDescription
                "https://image-url.com/img",  // productImageUrl
                2,                             // quantity
                unitPrice                      // unitPrice
        );

        // 2. Act
        cart.addItem(item);

        // 3. Assert
        assertFalse(cart.isEmpty());
        assertEquals(1, cart.getItemCount()); // 1 distinct item in the cart
        assertEquals(2, cart.getTotalItems()); // 2 total units

        // Total should be 2 units * 50.00 = 100.00
        Money expectedTotal = Money.of(new BigDecimal("100.00"));

        assertEquals(expectedTotal, cart.getTotalAmount());
    }
}
