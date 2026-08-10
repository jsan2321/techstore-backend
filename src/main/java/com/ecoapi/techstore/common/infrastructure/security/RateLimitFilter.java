package com.ecoapi.techstore.common.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Redis-backed fixed-window protection for public credential and payment calls. */
@Component
@ConditionalOnProperty(name = "app.security.rate-limit.enabled", havingValue = "true")
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Set<String> PROTECTED_PATHS = Set.of(
            "/api/v1/auth/login", "/api/v1/auth/google-login", "/api/v1/auth/register",
            "/api/v1/auth/forgot-password", "/api/v1/auth/resend-confirmation",
            "/api/v1/orders/paypal/create", "/api/v1/orders/paypal/capture");

    private final StringRedisTemplate redis;
    private final int limit;
    private final Duration window;

    public RateLimitFilter(
            StringRedisTemplate redis,
            @Value("${app.security.rate-limit.requests}") int limit,
            @Value("${app.security.rate-limit.window-seconds}") long windowSeconds) {
        this.redis = redis;
        this.limit = limit;
        this.window = Duration.ofSeconds(windowSeconds);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"POST".equalsIgnoreCase(request.getMethod())
                || !PROTECTED_PATHS.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String client = request.getHeader("X-Forwarded-For");
        if (client == null || client.isBlank()) {
            client = request.getRemoteAddr();
        } else {
            client = client.split(",", 2)[0].trim();
        }
        String key = "rate-limit:" + request.getRequestURI() + ":" + client;
        try {
            Long count = redis.opsForValue().increment(key);
            if (count != null && count == 1) {
                redis.expire(key, window);
            }
            if (count != null && count > limit) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":429,\"message\":\"Too many requests. Try again later.\"}");
                return;
            }
        } catch (RuntimeException ex) {
            // Availability of Redis is checked by Render; avoid turning a transient
            // telemetry dependency into a public error leak.
        }
        chain.doFilter(request, response);
    }
}
