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
        // 1. Arrange (Preparar)
        UserId userId = new UserId(1L); // Asumiendo que UserId recibe un Long o String

        // 2. Act (Actuar)
        Cart cart = Cart.createFor(userId);

        // 3. Assert (Comprobar)
        assertNotNull(cart);
        assertEquals(userId, cart.getUserId());
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getItemCount());
        // Asumiendo que tu clase Money tiene un metodo equals bien definido
        assertEquals(Money.zero(), cart.getTotalAmount());
    }

    @Test
    void shouldAddNewItemAndRecalculateTotal() {
        // 1. Arrange (Preparar)
        // Asumiendo que tu UserId y ProductId reciben un Long o String en su constructor
        UserId userId = new UserId(1L);
        Cart cart = Cart.createFor(userId);

        ProductId productId = new ProductId(100L);

        // Al usar BigDecimal, es mejor pasar un String ("50.00") que un double para evitar problemas de redondeo
        Money unitPrice = Money.of(new BigDecimal("50.00"));

        // Utilizamos el constructor real de tu CartItem respetando tus validaciones
        CartItem item = new CartItem(
                productId,
                "Laptop de Desarrollo",        // productName
                "Laptop con Linux instalado",  // productDescription
                "https://url-imagen.com/img",  // productImageUrl
                2,                             // quantity
                unitPrice                      // unitPrice
        );

        // 2. Act (Actuar)
        cart.addItem(item);

        // 3. Assert (Afirmar)
        assertFalse(cart.isEmpty());
        assertEquals(1, cart.getItemCount()); // Hay 1 producto distinto en el carrito
        assertEquals(2, cart.getTotalItems()); // Pero hay 2 unidades en total

        // El total debería ser 2 unidades * 50.00 = 100.00
        Money expectedTotal = Money.of(new BigDecimal("100.00"));

        // Esto funciona perfectamente porque sobreescribiste el método equals() en Money.java
        assertEquals(expectedTotal, cart.getTotalAmount());
    }

}
