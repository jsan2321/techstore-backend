package com.ecoapi.goodshopping.user.infrastructure.security.jwt;

import com.ecoapi.goodshopping.user.domain.service.TokenBlacklistService;
import com.ecoapi.goodshopping.user.infrastructure.security.SecurityUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter
 * Processes JWT tokens from Authorization header and sets SecurityContext
 * 
 * STATELESS APPROACH:
 * - Extracts user information (ID, email, roles) directly from JWT token
 * - Creates SecurityUser with information from token WITHOUT database query
 * - Faster than database lookup on every request
 * - Checks token blacklist for immediate revocation capability
 * 
 * TRADE-OFF:
 * - Pro: Better performance (no DB query per request)
 * - Con: Changes to user status (banned, role changes) only take effect after token expires
 * - Blacklist enables immediate token revocation for logout, password change, etc.
 */
@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
    
    private final JwtTokenProviderAdapter jwtUtils;
    private final TokenBlacklistService tokenBlacklistService;
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                    @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain) 
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateToken(jwt)) {
                // Check if token is blacklisted (for logout, password change, etc.)
                if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
                    logger.warn("Attempted to use blacklisted token");
                    filterChain.doFilter(request, response);
                    return;
                }
                
                // STATELESS: Extract all user information from JWT token
                Claims claims = jwtUtils.getClaimsFromToken(jwt);
                
                Long userId = extractUserId(claims);
                String email = claims.getSubject();
                @SuppressWarnings("unchecked")
                List<String> roles = claims.get("roles", List.class);
                
                // Create authorities from roles in JWT
                Collection<GrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                
                // Create SecurityUser with information from token (NO DATABASE QUERY)
                SecurityUser securityUser = new SecurityUser(
                        userId,
                        email,
                        "",  // Password not needed for JWT authentication
                        true,  // enabled
                        true,  // accountNonExpired
                        true,  // credentialsNonExpired
                        true,  // accountNonLocked
                        authorities
                );
                
                var authentication = new UsernamePasswordAuthenticationToken(
                        securityUser,
                        null,
                        securityUser.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
            // Don't set authentication, let the request proceed without auth
            // AuthenticationEntryPoint will handle unauthorized access
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extract user ID from JWT claims
     * Handles both String and Number types that may come from JWT
     */
    private Long extractUserId(Claims claims) {
        Object idClaim = claims.get("id");
        if (idClaim instanceof Number number) {
            return number.longValue();
        } else if (idClaim instanceof String string) {
            return Long.parseLong(string);
        }
        throw new IllegalArgumentException("User ID not found or invalid in JWT token");
    }
    
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
