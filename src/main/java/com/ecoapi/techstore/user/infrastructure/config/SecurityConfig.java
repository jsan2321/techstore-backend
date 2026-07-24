package com.ecoapi.techstore.user.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ecoapi.techstore.common.infrastructure.security.jwt.AuthTokenFilter;
import com.ecoapi.techstore.common.infrastructure.security.jwt.JwtAuthEntryPoint;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security Configuration for Hexagonal Architecture
 * Configures JWT-based stateless authentication with security best practices
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final UserDetailsService userDetailsService;
    private final JwtAuthEntryPoint authEntryPoint;
    private final AuthTokenFilter authTokenFilter;
    
    // List of URL patterns that require authentication
    private static final List<String> SECURED_URLS = List.of(
            "/api/v1/carts/**",
            "/api/v1/cartItems/**",
            "/api/v1/users/me",
            "/api/v1/users/me/password",
            "/api/v1/auth/logout",
            "/api/v1/admin/users/**",
            "/api/v1/admin/products/**",
            "/api/v1/admin/brands/**",
            "/api/v1/admin/categories/**",
            "/api/v1/admin/orders/**"
    );
    
    // OpenAPI/Swagger endpoints that should be publicly accessible
    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**"
    };
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) 
            throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF disabled for stateless JWT API
            .csrf(AbstractHttpConfigurer::disable)
            
            // CORS configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Exception handling
            .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPoint))
            
            // Stateless session management
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Security headers
            .headers(headers -> headers
                .frameOptions(frame -> frame.deny())
                .xssProtection(xss -> xss.disable())  // Handled by modern browsers
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
            )
            
            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                    // Swagger/OpenAPI endpoints - public access
                    .requestMatchers(SWAGGER_WHITELIST).permitAll()
                    // Secured endpoints - require authentication
                    .requestMatchers(SECURED_URLS.toArray(String[]::new))
                    .authenticated()
                    .anyRequest().permitAll()
            );
        
        // Authentication provider
        http.authenticationProvider(daoAuthenticationProvider());
        
        // JWT filter
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    /**
     * CORS configuration for cross-origin requests
     * TODO: Adjust allowed origins for production
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
