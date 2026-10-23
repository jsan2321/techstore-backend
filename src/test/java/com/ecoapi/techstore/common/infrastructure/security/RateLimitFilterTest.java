package com.ecoapi.techstore.common.infrastructure.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RateLimitFilterTest {

    private StringRedisTemplate redis;
    private ValueOperations<String, String> valueOperations;
    private RateLimitFilter filter;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        redis = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(valueOperations);
        filter = new RateLimitFilter(redis, 5, 60);
    }

    @Test
    void resolvesClientIpFromXRealIpWhenPresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Real-IP", "203.0.113.195");
        request.addHeader("X-Forwarded-For", "198.51.100.1, 198.51.100.2");
        request.setRemoteAddr("127.0.0.1");

        String clientIp = filter.resolveClientIp(request);

        assertThat(clientIp).isEqualTo("203.0.113.195");
    }

    @Test
    void resolvesRightmostIpFromXForwardedForWhenXRealIpIsAbsent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "spoofed.attacker.ip, 198.51.100.22");
        request.setRemoteAddr("127.0.0.1");

        String clientIp = filter.resolveClientIp(request);

        assertThat(clientIp).isEqualTo("198.51.100.22");
    }

    @Test
    void fallsBackToRemoteAddrWhenNoProxyHeadersPresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.50");

        String clientIp = filter.resolveClientIp(request);

        assertThat(clientIp).isEqualTo("192.168.1.50");
    }

    @Test
    void allowsRequestUnderRateLimit() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setRemoteAddr("192.168.1.100");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(valueOperations.increment(eq("rate-limit:/api/v1/auth/login:192.168.1.100"))).thenReturn(1L);

        filter.doFilterInternal(request, response, chain);

        verify(redis).expire(eq("rate-limit:/api/v1/auth/login:192.168.1.100"), any(Duration.class));
        verify(chain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void blocksRequestWhenRateLimitExceeded() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setRemoteAddr("192.168.1.100");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(valueOperations.increment(eq("rate-limit:/api/v1/auth/login:192.168.1.100"))).thenReturn(6L);

        filter.doFilterInternal(request, response, chain);

        verify(chain, never()).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getContentAsString()).contains("Too many requests");
    }
}
