package com.ecoapi.techstore.cart.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.techstore.cart.infrastructure.adapter.output.persistence.entity.CartEntity;
import com.ecoapi.techstore.config.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class JpaCartRepositoryTest {

    @Autowired
    private JpaCartRepository repository;

    @Test
    void savesCartAndFindsByUserId() {
        CartEntity cart = new CartEntity();
        cart.setUserId(88L);
        cart.setTotalAmount(new BigDecimal("99.99"));

        repository.save(cart);

        Optional<CartEntity> found = repository.findByUserId(88L);
        assertThat(found).isPresent();
        assertThat(found.get().getTotalAmount()).isEqualByComparingTo("99.99");
        assertThat(repository.existsByUserId(88L)).isTrue();
        assertThat(repository.existsByUserId(999L)).isFalse();
    }
}
