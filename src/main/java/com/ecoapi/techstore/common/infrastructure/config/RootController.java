package com.ecoapi.techstore.common.infrastructure.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Provides a lightweight root endpoint so navigating directly to the backend
 * service URL returns an informative health/status response instead of a 404 or 500 error.
 */
@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "TechStore API",
                "health", "/actuator/health",
                "api", "/api/v1"
        ));
    }
}
