package com.ecoapi.techstore.product.domain.model;

import com.ecoapi.techstore.common.domain.valueobjects.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    private Brand createSampleBrand() {
        return new Brand("TechBrand");
    }

    private Category createSampleCategory() {
        return new Category("Electronics");
    }

    @Test
    void createsProductWithValidAttributes() {
        Product product = Product.create(
                "Gaming Laptop",
                createSampleBrand(),
                Money.of(new BigDecimal("1200.00")),
                10,
                "High performance gaming laptop",
                createSampleCategory()
        );

        assertThat(product.getName()).isEqualTo("Gaming Laptop");
        assertThat(product.getStock()).isEqualTo(10);
        assertThat(product.isActive()).isTrue();
        assertThat(product.hasStock(5)).isTrue();
        assertThat(product.hasStock(15)).isFalse();
    }

    @Test
    void managesStockAddAndReduce() {
        Product product = Product.create(
                "Gaming Laptop",
                createSampleBrand(),
                Money.of(new BigDecimal("1200.00")),
                10,
                "High performance gaming laptop",
                createSampleCategory()
        );

        product.addStock(5);
        assertThat(product.getStock()).isEqualTo(15);

        product.reduceStock(7);
        assertThat(product.getStock()).isEqualTo(8);

        assertThatThrownBy(() -> product.reduceStock(20))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    void validatesDiscountPercentage() {
        Product product = Product.create(
                "Gaming Laptop",
                createSampleBrand(),
                Money.of(new BigDecimal("1200.00")),
                10,
                "High performance gaming laptop",
                createSampleCategory()
        );

        product.applyDiscount(20);
        assertThat(product.getDiscountPercentage()).isEqualTo(20);

        assertThatThrownBy(() -> product.applyDiscount(150))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
