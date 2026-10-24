package com.ecoapi.techstore.user.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

class SecurityConfigCorsTest {

    @Test
    void corsConfigurationSourceAllowsWildcardWithCredentialsWithoutThrowing() {
        SecurityConfig config = new SecurityConfig(null, null, null, null);
        ReflectionTestUtils.setField(config, "allowedOrigins", "*");

        CorsConfigurationSource source = config.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/google-login");

        CorsConfiguration resolved = source.getCorsConfiguration(request);
        assertThat(resolved).isNotNull();
        assertThat(resolved.getAllowCredentials()).isTrue();

        // Validates that checkOrigin does not throw IllegalArgumentException when wildcard pattern is used
        assertThatNoException().isThrownBy(() -> {
            String origin = resolved.checkOrigin("https://techstore-frontend-q5lz.onrender.com");
            assertThat(origin).isEqualTo("https://techstore-frontend-q5lz.onrender.com");
        });
    }

    @Test
    void corsConfigurationSourceAllowsSpecificOriginWithCredentials() {
        SecurityConfig config = new SecurityConfig(null, null, null, null);
        ReflectionTestUtils.setField(config, "allowedOrigins", "https://techstore-frontend-q5lz.onrender.com, http://localhost:4200");

        CorsConfigurationSource source = config.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/auth/google-login");

        CorsConfiguration resolved = source.getCorsConfiguration(request);
        assertThat(resolved).isNotNull();
        assertThat(resolved.getAllowCredentials()).isTrue();

        String allowedOrigin = resolved.checkOrigin("https://techstore-frontend-q5lz.onrender.com");
        assertThat(allowedOrigin).isEqualTo("https://techstore-frontend-q5lz.onrender.com");

        String forbiddenOrigin = resolved.checkOrigin("https://evil.com");
        assertThat(forbiddenOrigin).isNull();
    }
}
