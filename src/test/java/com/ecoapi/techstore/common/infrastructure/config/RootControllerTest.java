package com.ecoapi.techstore.common.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RootControllerTest {

    @Test
    void rootReturnsUpStatus() {
        RootController controller = new RootController();
        ResponseEntity<Map<String, Object>> response = controller.root();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("UP");
        assertThat(response.getBody().get("service")).isEqualTo("TechStore API");
    }
}
