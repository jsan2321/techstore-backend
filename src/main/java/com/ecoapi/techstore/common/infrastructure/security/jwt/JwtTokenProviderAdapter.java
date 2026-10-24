package com.ecoapi.techstore.common.infrastructure.security.jwt;

import com.ecoapi.techstore.common.application.port.out.TokenProviderPort;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

/**
 * JWT Token Provider Adapter
 * Implements token generation for authentication using JWT and provides token validation and extraction methods
 */
@Component
public class JwtTokenProviderAdapter implements TokenProviderPort {
        
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProviderAdapter.class);
    
    @Value("${auth.token.jwtSecret}")
    private String jwtSecret;
    
    @Value("${auth.token.expirationInMils}")
    private int expirationTime;
    
    @Override
    public String generateToken(String userId, String email, List<String> roles) {
        
        return Jwts.builder()
                .subject(email)
                .claim("id", userId)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + expirationTime))
                .signWith(key())
                .compact();
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("JWT error: {}", e.getMessage());
            return false;
        }
    }
    
    // If ever want to switch to a "Trust Token" mode
    @Override
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        Object idClaim = claims.get("id");
        if (idClaim instanceof String) {
            return (String) idClaim;
        }
        return String.valueOf(idClaim);
    }

    @Override
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    
    /**
     * Extract all claims from JWT token
     * Used by AuthTokenFilter for stateless authentication
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * Get the expiration date from a JWT token
     * @param token The JWT token
     * @return The expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }
    
    private Key key() {
        byte[] keyBytes;
        try {
            byte[] decoded = Decoders.BASE64.decode(jwtSecret);
            keyBytes = decoded.length >= 32 ? decoded : sha256(jwtSecret);
        } catch (RuntimeException ex) {
            byte[] rawBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
            keyBytes = rawBytes.length >= 32 ? rawBytes : sha256(jwtSecret);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private byte[] sha256(String secret) {
        try {
            return MessageDigest.getInstance("SHA-256")
                    .digest(secret.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
