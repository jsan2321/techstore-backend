package com.ecoapi.techstore.product.infrastructure.adapter.output.persistence;

import com.ecoapi.techstore.config.TestcontainersConfiguration;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.entity.ProductEntity;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.repository.JpaProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class ProductRepositoryTest {

    @Autowired
    private JpaProductRepository repository;

    @Test
    void savesAndFindsProductByExistsByName() {
        ProductEntity product = new ProductEntity();
        product.setName("Wireless Noise-Canceling Headphones");
        product.setPrice(new BigDecimal("299.99"));
        product.setStock(15);
        product.setActive(true);

        repository.save(product);

        assertThat(repository.existsByName("Wireless Noise-Canceling Headphones")).isTrue();
        assertThat(repository.existsByName("Nonexistent Product")).isFalse();
    }
}
