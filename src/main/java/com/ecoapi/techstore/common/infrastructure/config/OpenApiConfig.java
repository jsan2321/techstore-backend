package com.ecoapi.techstore.common.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger Configuration
 * Provides API documentation with JWT Bearer authentication support
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Development Server"),
                        new Server()
                                .url("https://api.techstore.com")
                                .description("Production Server")
                ))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme()))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }

    private Info apiInfo() {
        return new Info()
                .title("TechStore API")
                .description("""
                        ## TechStore E-Commerce REST API
                        
                        A comprehensive e-commerce backend API built with **Spring Boot** following **Hexagonal Architecture** principles.
                        
                        ### Features
                        - **User Management**: Registration, authentication, profile management
                        - **Product Catalog**: Browse products, categories, and brands
                        - **Shopping Cart**: Add/remove items, update quantities
                        - **Order Management**: Place orders, track order status
                        
                        ### Authentication
                        This API uses **JWT Bearer Token** authentication. To access protected endpoints:
                        1. Register a new account or login with existing credentials
                        2. Copy the `accessToken` from the response
                        3. Click the **Authorize** button and enter: `Bearer <your-token>`
                        
                        ### API Versioning
                        All endpoints are prefixed with `/api/v1`
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("TechStore Team")
                        .email("support@techstore.com")
                        .url("https://techstore.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0"));
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter your JWT token. Example: <your-jwt-access-token>");
    }
}
